package com.coursehub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveOtp(String userIdentifier, String otp) {
        redisTemplate.opsForValue().set("otp:" + userIdentifier, otp, 1, TimeUnit.MINUTES);
    }

    public String getOtp(String userIdentifier) {
        return (String) redisTemplate.opsForValue().get("otp:" + userIdentifier);
    }
}
