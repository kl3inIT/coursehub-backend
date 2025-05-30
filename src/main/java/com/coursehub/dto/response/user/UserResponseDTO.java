package com.coursehub.dto.response.user;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponseDTO {

    private int id;
    private String email;
    private String name;
    private String fullName;
    private List<String> roles;

}
