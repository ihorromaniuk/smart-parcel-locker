package core.basesyntax.userservice.service;

import core.basesyntax.userservice.dto.user.UpdateUserEmailRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserInfoRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserPasswordRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserRoleRequestDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.exceptions.EntityNotFoundException;
import core.basesyntax.userservice.exceptions.RegistrationException;
import core.basesyntax.userservice.mapper.UserMapper;
import core.basesyntax.userservice.model.Role;
import core.basesyntax.userservice.model.User;
import core.basesyntax.userservice.repository.RoleRepository;
import core.basesyntax.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto updateUserInfo(UpdateUserInfoRequestDto updateUserInfoRequestDto,
                                          User user) {
        user.setFullName(updateUserInfoRequestDto.fullName());
        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponseDto updateUserRole(UpdateUserRoleRequestDto requestDto) {
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
}
