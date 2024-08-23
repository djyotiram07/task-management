package com.example.service;

import com.example.dto.UserDto;
import com.example.dto.RoleDto;
import com.example.event.UserCreateEvent;
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
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserCreateEvent> kafkaTemplate1;
    private final KafkaTemplate<String, UserDeleteEvent> kafkaTemplate2;

    @PostConstruct
    private void addRoles() {
        roleRepository.save(new Role(1L, "USER"));
        roleRepository.save(new Role(2L, "ADMIN"));
    }

    @KafkaListener(topics = "registerUser")
    public void register(UserRegisterEvent userRegisterEvent) {

        if (userRepository.existsByEmail(userRegisterEvent.email())) {
            throw new UserAlreadyExistsException("User with email " + userRegisterEvent.email() + " already exists.");
        }

        try {
            User user = User.builder()
                    .id(userRegisterEvent.id())
                    .username(userRegisterEvent.username())
                    .email(userRegisterEvent.email())
                    .roles(userRegisterEvent.roles())
                    .password(userRegisterEvent.password())
                    .build();

            User savedUser = userRepository.save(user);
            sendUserCreateEvent(savedUser);
            log.info("User registered with email: {}", userRegisterEvent.email());
        } catch (Exception e) {
            throw new UserCommonException("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public UserDto findUserById(Long id) {

        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::userToUserDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public void updateUserById(Long id, UserDto userDto) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        try {
            User updateUser = userMapper.userDtoToUser(userDto);
            updateUser.setId(id);
            userRepository.save(updateUser);

            log.info("User updated with id: {}", id);
        } catch (Exception e) {
            throw new UserCommonException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUserById(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        try {
            userRepository.deleteById(id);
            kafkaTemplate2.send("deleteUser", new UserDeleteEvent(id));
            log.info("User deleted with id: {}", id);
        } catch (Exception e) {
            throw new UserCommonException("Error deleting user : " + id);
        }
    }

    @Override
    public Page<UserDto> findAllUser(Pageable pageable) {

        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::userToUserDto);
    }

    @Override
    public void assignRoleToUser(Long id, RoleDto roleDto) {

        Optional<Role> role = roleRepository.findByName(roleDto.name());
        Optional<User> user = userRepository.findById(id);

        if (role.isPresent() && user.isPresent()) {
            user.get().getRoles().add(role.get());
            userRepository.save(user.get());

            log.info("Role {} assigned to user with id: {}", roleDto.name(), id);
        } else {
            throw new UserNotFoundException("User or role not found : " + id);
        }
    }

    private void sendUserCreateEvent(User user) {
        UserCreateEvent userCreateEvent = new UserCreateEvent(user.getId(), user.getEmail());
        kafkaTemplate1.send("createUser", userCreateEvent);
    }
}
