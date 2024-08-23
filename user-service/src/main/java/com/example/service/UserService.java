package com.example.service;

import com.example.dto.RoleDto;
import com.example.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserDto findUserById(Long id);

    void updateUserById(Long id, UserDto userDto);

    void deleteUserById(Long id);

    Page<UserDto> findAllUser(Pageable pageable);

    void assignRoleToUser(Long id, RoleDto roleDto);
}
