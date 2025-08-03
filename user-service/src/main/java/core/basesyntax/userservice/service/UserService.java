package core.basesyntax.userservice.service;

import core.basesyntax.userservice.dto.user.CreateUserRequestDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.exceptions.EntityNotFoundException;
import core.basesyntax.userservice.exceptions.RegistrationException;
import core.basesyntax.userservice.mapper.UserMapper;
import core.basesyntax.userservice.model.Role;
import core.basesyntax.userservice.model.User;
import core.basesyntax.userservice.repository.RoleRepository;
import core.basesyntax.userservice.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto registerNewUser(CreateUserRequestDto requestDto) {
        User user = userMapper.toModel(requestDto);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RegistrationException("User with email "
                    + user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(requestDto.password()));
        Role userRole = roleRepository.findByName(Role.RoleName.USER).orElseThrow(() ->
                new EntityNotFoundException("Can't find role by name. "
                        + "Name: " + Role.RoleName.USER));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
