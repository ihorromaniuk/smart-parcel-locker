package core.basesyntax.userservice.service;

import core.basesyntax.userservice.config.RabbitMqConfig;
import core.basesyntax.userservice.dto.user.DisableUserRequestDto;
import core.basesyntax.userservice.dto.user.EnableUserRequestDto;
import core.basesyntax.userservice.dto.user.RestoreUserRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserEmailRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserInfoRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserPasswordRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserRoleRequestDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.exceptions.EntityNotFoundException;
import core.basesyntax.userservice.exceptions.RegistrationException;
import core.basesyntax.userservice.exceptions.AdminSelfChangeException;
import core.basesyntax.userservice.mapper.UserMapper;
import core.basesyntax.userservice.model.Role;
import core.basesyntax.userservice.model.User;
import core.basesyntax.userservice.repository.RoleRepository;
import core.basesyntax.userservice.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_DISABLED_ROUTING_KEY = "user.disabled";
    private static final String USER_ENABLED_ROUTING_KEY = "user.enabled";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final EntityManager entityManager;

    public UserResponseDto updateUserInfo(UpdateUserInfoRequestDto updateUserInfoRequestDto,
                                          User user) {
        user.setFullName(updateUserInfoRequestDto.fullName());
        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponseDto updateUserRole(UpdateUserRoleRequestDto requestDto, User currentUser) {
        if (requestDto.email().equals(currentUser.getEmail())) {
            throw new AdminSelfChangeException("Can't change role for yourself. Ask another admin");
        }
        Role role = roleRepository
                .findByName(Role.RoleName.valueOf(requestDto.role())).orElseThrow(() ->
                new EntityNotFoundException("Can't find role by name: " + requestDto.role()));
        User user = userRepository.findByEmail(requestDto.email()).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email " + requestDto.email()));
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserResponseDto updateUserEmail(UpdateUserEmailRequestDto requestDto,
                                           User user) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RegistrationException("User with email "
                    + requestDto.email() + " already exists");
        }

        user.setEmail(requestDto.email());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserResponseDto updateUserPassword(UpdateUserPasswordRequestDto requestDto,
                                              User user) {
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserResponseDto getInfoAboutUser(User user) {
        return userMapper.toDto(user);
    }

    public UserResponseDto disableUser(DisableUserRequestDto requestDto, User currentUser) {
        if (requestDto.email().equals(currentUser.getEmail())) {
            throw new AdminSelfChangeException("Can't disable yourself. Ask another admin");
        }
        User user = userRepository.findByEmail(requestDto.email()).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email. "
                        + "Email: " + requestDto.email()));
        user.setEnabled(false);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_NAME,
                USER_DISABLED_ROUTING_KEY,
                userMapper.modelToDisabledEvent(user));
        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponseDto enableUser(@Valid EnableUserRequestDto requestDto, User currentUser) {
        if (requestDto.email().equals(currentUser.getEmail())) {
            throw new AdminSelfChangeException("Can't enable yourself. Ask another admin");
        }
        User user = userRepository.findByEmail(requestDto.email()).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email. "
                        + "Email: " + requestDto.email()));
        user.setEnabled(true);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_NAME,
                USER_ENABLED_ROUTING_KEY,
                userMapper.modelToEnabledEvent(user));
        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can't find user by id. Id: " + id);
        }
    }

    @Transactional
    public UserResponseDto restoreUserByEmail(RestoreUserRequestDto requestDto) {
        if (!userRepository.existsAnyByEmail(requestDto.email())) {
            throw new EntityNotFoundException("Can't find any user by email. "
                    + "Email: " + requestDto.email());
        }
        userRepository.restoreUserByEmail(requestDto.email());
        User user = userRepository.findByEmail(requestDto.email()).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email. "
                        + "Email: " + requestDto.email()));
        Role userRole = roleRepository.findByName(Role.RoleName.USER).orElseThrow(() ->
                new EntityNotFoundException("Can't find role by name. Name: "
                        + Role.RoleName.USER));
        user.getRoles().add(userRole);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
