package com.coursehub.service;

import com.coursehub.exception.auth.EmailSendingException;
import com.coursehub.exception.auth.RedisOperationException;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender mailSender;



    // Tạo OTP ngẫu nhiên
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // OTP 6 chữ số
        return String.valueOf(otp);
    }

    // Gửi OTP qua email
    public void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp + ". It is valid for 1 minutes.");
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send OTP email", e);
        }
    }

}
