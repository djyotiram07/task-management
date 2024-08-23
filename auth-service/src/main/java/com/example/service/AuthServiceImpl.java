package com.example.service;

import com.example.dto.RoleDto;
import com.example.dto.UserDto;
import com.example.event.UserDeleteEvent;
import com.example.event.UserRegisterEvent;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.exceptions.UserCommonException;
import com.example.exceptions.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.utils.AuthenticationRequest;
import com.example.utils.AuthenticationResponse;
import com.example.utils.JwtService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserRegisterEvent> kafkaTemplate;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void addRoles() {
        roleRepository.save(new Role(1L, "USER"));
        roleRepository.save(new Role(2L, "ADMIN"));
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        var jwtToken = jwtService.generateJwtToken(user);
        return new AuthenticationResponse(user.getId(), jwtToken);
    }

    @Override
    public AuthenticationResponse register(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.email())) {
            throw new UserAlreadyExistsException("User with email " + userDto.email() + " already exists.");
        }

        try {
            User user = userMapper.userDtoToUser(userDto);
            user.setPassword(passwordEncoder.encode(userDto.password()));

            Set<Role> roles = new HashSet<>();
            for (RoleDto roleDto : userDto.roles()) {
                Role role = roleRepository.findById(roleDto.id())
                        .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleDto.name()));
                roles.add(role);
            }
            user.setRoles(roles);

            User savedUser = userRepository.save(user);
            sendUserCreateEvent(savedUser);

            log.info("User registered with email: {}", userDto.email());
            var jwtToken = jwtService.generateJwtToken(user);

            return new AuthenticationResponse(savedUser.getId(), jwtToken);
        } catch (Exception e) {
            throw new UserCommonException("Error registering user: " + e.getMessage());
        }
    }

    private void sendUserCreateEvent(User user) {
        UserRegisterEvent userRegisterEvent = new UserRegisterEvent(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getRoles());
        kafkaTemplate.send("registerUser", userRegisterEvent);
    }

    @KafkaListener(topics = "deleteUser")
    public void userDeleteListener(UserDeleteEvent userDeleteEvent) {
        log.info("Received delete message from user service: {}", userDeleteEvent);

        // change to deleteByEmail if necessary
        if (!userRepository.existsById(userDeleteEvent.id())) {
            throw new UserNotFoundException("User not found with id: " + userDeleteEvent);
        }

        try {
            userRepository.deleteById(userDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", userDeleteEvent.id(), e.getMessage());
        }
    }
}
