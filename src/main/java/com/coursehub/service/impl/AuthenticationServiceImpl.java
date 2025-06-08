package com.coursehub.service.impl;

import com.coursehub.components.OtpUtil;
import com.coursehub.converter.AuthenticationConverter;
import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.auth.*;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.InvalidTokenEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exceptions.auth.*;
import com.coursehub.repository.InvalidTokenRepository;
import com.coursehub.repository.RoleRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.AuthenticationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidTokenRepository invalidTokenRepository;
    private final RoleRepository roleRepository;
    private final OtpUtil otpUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserConverter userConverter;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthenticationConverter authenticationConverter;

  @Override
  public AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO) {
      AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();
      String email = authenticationRequestDTO.getEmail();
      String googleAccountId = authenticationRequestDTO.getGoogleAccountId();

      UserEntity userByEmail = userRepository.findByEmailAndIsActive(email, 1L);
      // dang nhap local
      if (googleAccountId == null) {
          if (userByEmail == null) {
              throw new DataNotFoundException("User not found");
          }
          if (!passwordEncoder.matches(authenticationRequestDTO.getPassword(), userByEmail.getPassword())) {
              throw new PasswordNotMatchException("Password not match");
          }
          authenticationResponseDTO.setToken(generateToken(userByEmail));
          return authenticationResponseDTO;
      }

      // dang nhap bang tai khoan google
      UserEntity userByGoogle = userRepository.findByGoogleAccountIdAndIsActive(googleAccountId, 1L);

      if (userByGoogle == null) {
          if (userByEmail == null) {
              // Tạo mới user từ Google
              userByGoogle = userConverter.toUserEntity(authenticationRequestDTO);
              userByGoogle.setRoleEntity(roleRepository.findByCode("LEARNER"));
              userRepository.save(userByGoogle);
          } else {
              // Liên kết tài khoản Google với user local
              userByEmail.setGoogleAccountId(googleAccountId);
              userRepository.save(userByEmail);
              userByGoogle = userByEmail;
          }
      }
      authenticationResponseDTO.setToken(generateToken(userByGoogle));
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
        } catch (ParseException e) {
            throw new InvalidTokenException("Token is not valid");
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
            if (!(isValid && expiryTime.after(new Date()) &&
                    !invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))) {
                throw new InvalidTokenException("Token is not valid");
            }
            return true;

        } catch (JOSEException | ParseException e) {
            throw new InvalidTokenException("Token is not valid");
        }
    }

    @Override
    public String initUser(UserRequestDTO userDTO) {
        if (userRepository.findByEmailAndIsActive(userDTO.getEmail(), 1L) != null) {
            throw new IllegalEmailException("Email is illegal");
        }

        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
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
        UserEntity userEntity = userConverter.toUserEntity(userRequestDTO);
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        userEntity.setPassword(encodedPassword);
        userEntity.setRoleEntity(roleRepository.findByCode("LEARNER"));
        userRepository.save(userEntity);
        return userConverter.toUserResponseDTO(userEntity);
    }

    @Override
    public String reSendOtp(OtpRequestDTO otpRequestDTO) {
        String email = otpRequestDTO.getEmail();


        if (userRepository.findByEmailAndIsActive(email, 1L) != null || Boolean.TRUE.equals(redisTemplate.hasKey(email))) {
            throw new IllegalEmailException("Email is illegal");
        }
        // tao va gui otp
        redisTemplate.delete("otp:" + email);

        String otp = otpUtil.generateOtp();
        saveToRedis("otp:" + email, otp);
        otpUtil.sendOtpEmail(email, otp);
        return "Otp is re sent to " + email;
    }

    @Override
    public String sendOtpToResetPassword(OtpRequestDTO otpRequestDTO) {
        String email = otpRequestDTO.getEmail();
        if (userRepository.findByEmailAndIsActive(email, 1L) == null) {
            throw new IllegalEmailException("User not found with email: " + otpRequestDTO.getEmail());
        }
        String otp = otpUtil.generateOtp();
        saveToRedis("otp:" + email, otp);
        otpUtil.sendOtpEmail(email, otp);
        return "Otp is re sent to " + email;
    }

    @Override
    public String verifyOtpToResetPassword(OtpRequestDTO otpRequestDTO) {
        String storedOtp = (String) getFromRedis("otp:" + otpRequestDTO.getEmail());

        if (storedOtp == null) {
            throw new OtpNotFoundException("Otp not found");
        }
        if (!storedOtp.equals(otpRequestDTO.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        return "Successfully verified OTP. You can now reset your password.";
    }

    @Override
    public String resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        UserEntity user = userRepository.findByEmailAndIsActive(resetPasswordRequestDTO.getEmail(), 1L);
        if (user == null) {
            throw new IllegalEmailException("User not found with email: " + resetPasswordRequestDTO.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(resetPasswordRequestDTO.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "Change password successfully";
    }


    @Override
    public String generateGoogleUrl() {
        // Lấy thông tin client registration cho Google
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        // Xây dựng URL thủ công
        return googleRegistration.getProviderDetails().getAuthorizationUri() +
                "?client_id=" + googleRegistration.getClientId() +
                "&redirect_uri=" + googleRegistration.getRedirectUri() +
                "&response_type=code" +
                "&scope=" + String.join(" ", googleRegistration.getScopes()) +
                "&access_type=offline" +
                "&prompt=consent";
    }

    @Override
    public AuthenticationRequestDTO handleGoogleCode(GoogleCodeRequestDTO googleCodeRequestDTO) throws IOException {

        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");

        // Khởi tạo RestTemplate để thực hiện các HTTP request
        RestTemplate restTemplate = new RestTemplate();
        // Đặt request factory để hỗ trợ các phương thức HTTP nâng cao (như PATCH)
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // Sử dụng authorization code từ frontend để lấy access token từ Google
        String accessToken = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(), new GsonFactory(),
                googleRegistration.getClientId(),
                googleRegistration.getClientSecret(),
                googleCodeRequestDTO.getCode(),
                googleRegistration.getRedirectUri()
        ).execute().getAccessToken();

        // Thêm interceptor để tự động gắn access token vào header Authorization cho các request tiếp theo
        restTemplate.getInterceptors().add(
                (request, body, execution) -> {
                    request.getHeaders().setBearerAuth(accessToken);
                    return execution.execute(request, body);
                }
        );

        // Gửi request đến endpoint user info của Google, parse kết quả JSON thành Map
        Map<String, Object> data = new ObjectMapper().readValue(
                restTemplate.getForEntity(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri(), String.class).getBody(),
                new TypeReference<>() {
                }
        );


        return authenticationConverter.toAuthenticationRequestDTO(data);

    }

    public void saveToRedis(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value, 1, TimeUnit.MINUTES);
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
                .claim("name", user.getName())
                .issuer("coursehub.com")
                .claim("name", user.getName())
                .claim("avatar", user.getAvatar())
                .issueTime(new Date())
                .claim("name", user.getName())
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .claim("scope", user.getRoleEntity().getCode())
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secret.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new GenerateTokenException("Failed to generate token");
        }

    }

    public void deleteFromRedis(String key) {
        redisTemplate.delete(key);
    }


}
