package com.example.controller;

import com.example.dto.RoleDto;
import com.example.dto.UserDto;
import com.example.exceptions.UserCommonException;
import com.example.service.UserService;
import com.example.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tm-user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable Long id) {
        UserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @RequestBody UserDto userDto) {

        userService.updateUserById(id, userDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestHeader(value = "Authorization") String token) {
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new UserCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long id,
                                                 @RequestBody RoleDto roleDto,
                                                 @RequestHeader(value = "Authorization") String token) {

        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new UserCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        userService.assignRoleToUser(id, roleDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> findAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserDto> users = userService.findAllUser(PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }
}
