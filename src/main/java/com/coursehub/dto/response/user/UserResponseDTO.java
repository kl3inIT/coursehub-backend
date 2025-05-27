package com.coursehub.dto.response.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {

    int id;
    String username;
    String fullName;
    String email;

}
