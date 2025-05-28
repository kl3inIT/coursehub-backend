package com.coursehub.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequestDTO {
    @NotBlank(message = "Token can not be blank")
    private String token;
}
