package com.coursehub.service;

import com.coursehub.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
}
