package com.coursehub.service.impl;

import com.coursehub.components.OtpUtil;
import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.auth.TokenRequestDTO;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.InvalidTokenEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.entity.UserRoleEntity;
import com.coursehub.exception.auth.*;
import com.coursehub.repository.InvalidTokenRepository;
import com.coursehub.repository.RoleRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidTokenRepository invalidTokenRepository;
    private final RoleRepository roleRepository;
    private final OtpUtil otpUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserConverter userConverter;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO){
        UserEntity user = userRepository.findByEmailAndIsActive(authenticationRequestDTO.getEmail(), 1L);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }

        if(!passwordEncoder.matches(authenticationRequestDTO.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("Password not match");
        }

        String token = generateToken(user);
        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();
        authenticationResponseDTO.setToken(token);
        return authenticationResponseDTO;
    }

    @Override
    public String logout(TokenRequestDTO tokenRequestDTO) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(tokenRequestDTO.getToken());
            InvalidTokenEntity invalidTokenEntity = new InvalidTokenEntity();
            invalidTokenEntity.setId(signedJWT.getJWTClaimsSet().getJWTID());
            invalidTokenEntity.setExpiryTime(signedJWT.getJWTClaimsSet().getExpirationTime());
            invalidTokenRepository.save(invalidTokenEntity);
            return "Successfully logged out";
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyToken(String token) {
        try {
            // xac thuc neu token da het han hoac co trong bang token het han
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret.getBytes());
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean isValid = signedJWT.verify(verifier);
            if(!(isValid && expiryTime.after(new Date()) &&
                    !invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))) {
                throw new InvalidTokenException("Token is not valid");
            }
            return true;

        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String initUser(UserRequestDTO userDTO) {
        if(userRepository.findByEmailAndIsActive(userDTO.getEmail(), 1L) != null){
            throw new IllegalEmailException("Email is illegal");
        }

        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            throw new PasswordNotMatchException("Password not match");
        }

        // luu tam user vao redis
        saveToRedis("user:" + userDTO.getEmail(), userDTO);

        // tao va gui otp
        String otp = otpUtil.generateOtp();
        saveToRedis("otp:" + userDTO.getEmail(), otp);
        otpUtil.sendOtpEmail(userDTO.getEmail(), otp);
        return "Otp is sent to " + userDTO.getEmail();

    }

    @Override
    public UserResponseDTO verifyUser(OtpRequestDTO otpRequestDTO) {
        String storedOtp = (String) getFromRedis("otp:" + otpRequestDTO.getEmail());

        if (storedOtp == null) {
            throw new OtpNotFoundException("Otp not found");
        }
        if (!storedOtp.equals(otpRequestDTO.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        UserRequestDTO userRequestDTO = (UserRequestDTO) getFromRedis("user:" + otpRequestDTO.getEmail());
        UserEntity  userEntity = userConverter.toUserEntity(userRequestDTO);
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        userEntity.setPassword(encodedPassword);
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserEntity(userEntity);
        userRoleEntity.setRoleEntity(roleRepository.findByCode("LEARNER"));
        userEntity.setUserRoleEntities(Collections.singleton(userRoleEntity));
        userRepository.save(userEntity);
        return userConverter.toUserResponseDTO(userEntity);
    }

    @Override
    public String reSendOtp(OtpRequestDTO otpRequestDTO) {
        String email = otpRequestDTO.getEmail();


        if(userRepository.findByEmailAndIsActive(email, 1L) != null || Boolean.TRUE.equals(redisTemplate.hasKey(email))){
            throw new IllegalEmailException("Email is illegal");
        }
        // tao va gui otp
        deleteFromRedis("otp:" + email);

        String otp = otpUtil.generateOtp();
        saveToRedis("otp:" + email, otp);
        otpUtil.sendOtpEmail(email, otp);
        return "Otp is re sent to " + email;
    }

    public void saveToRedis(String key, Object value) {
        try {
            redisTemplate.opsForValue().set( key, value, 1, TimeUnit.MINUTES);
        } catch (RedisConnectionException e) {
            throw new RedisOperationException("Failed to save " + key + " to Redis", e);
        }
    }

    public Object getFromRedis(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    private String generateToken(UserEntity user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("coursehub.com")
                .issueTime(new Date())
                .claim("name", user.getName())
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .claim("scope", getScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secret.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    public String getScope(UserEntity user) {
        StringJoiner joiner = new StringJoiner(" ");
        user.getUserRoleEntities().forEach(userRoleEntity -> joiner.add(userRoleEntity.getRoleEntity().getCode()));
        return joiner.toString();
    }

    public void deleteFromRedis(String key) {

        redisTemplate.delete(key);
    }


}
