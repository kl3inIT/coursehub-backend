package com.coursehub.service.impl;

import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.auth.TokenRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.entity.InvalidTokenEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exception.auth.DataNotFoundException;
import com.coursehub.exception.auth.InvalidTokenException;
import com.coursehub.exception.auth.PasswordNotMatchException;
import com.coursehub.repository.InvalidTokenRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidTokenRepository invalidTokenRepository;

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

    private String generateToken(UserEntity user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("coursehub.com")
                .issueTime(new Date())
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
        user.getUserRoleEntityList().forEach(userRoleEntity -> joiner.add(userRoleEntity.getRoleEntity().getCode()));
        return joiner.toString();
    }

}
