package com.coursehub.converter;

import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthenticationConverter {

    public AuthenticationRequestDTO toAuthenticationRequestDTO(Map<String, Object> data) {
        AuthenticationRequestDTO dto = new AuthenticationRequestDTO();
        dto.setEmail((String) data.get("email"));
        dto.setName((String) data.get("name"));
        dto.setAvatar((String) data.get("picture"));
        dto.setPhone("");
        dto.setPassword("");
        dto.setGoogleAccountId((String) data.get("sub"));
        return dto;
    }

}
