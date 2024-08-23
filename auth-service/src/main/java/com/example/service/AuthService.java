package com.example.service;

import com.example.dto.UserDto;
import com.example.utils.AuthenticationRequest;
import com.example.utils.AuthenticationResponse;

public interface AuthService {

    AuthenticationResponse register(UserDto userDto);

    AuthenticationResponse login(AuthenticationRequest request);
}
