package com.example.controller;

import com.example.dto.UserDto;
import com.example.service.AuthService;
import com.example.utils.AuthenticationRequest;
import com.example.utils.AuthenticationResponse;
import com.example.utils.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final JwtService jwtService;

    @PostMapping(value = "/register")
    ResponseEntity<AuthenticationResponse> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(service.register(userDto));
    }

    @PostMapping(value = "/login")
    ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

//    @GetMapping("/logout")
//    public String customLogout(HttpServletRequest request,
//                               HttpServletResponse response,
//                               @RequestHeader("Authorization") String token) {
//        System.out.println("------------------------------auth logout-----------------------");
//        if (token != null && !token.isEmpty()) {
//            System.out.println("Token: " + token);
//            System.out.println("Username: " + jwtService.extractUsername(token));
//            System.out.println("Roles: " + jwtService.extractRoles(token));
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            System.out.println("Authentication details: " + authentication.getDetails());
//            System.out.println("Authentication principal: " + authentication.getPrincipal());
//
//            SecurityContextHolder.clearContext();
//        }
//        return "redirect:/login";
//    }

}
