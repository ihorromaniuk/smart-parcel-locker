package core.basesyntax.userservice.service;

import core.basesyntax.userservice.config.RabbitMqConfig;
import core.basesyntax.userservice.dto.user.CreateUserRequestDto;
import core.basesyntax.userservice.dto.user.LoginRequestDto;
import core.basesyntax.userservice.dto.user.LoginResponseDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.exceptions.EntityNotFoundException;
import core.basesyntax.userservice.exceptions.RegistrationException;
import core.basesyntax.userservice.mapper.UserMapper;
import core.basesyntax.userservice.model.Role;
import core.basesyntax.userservice.model.User;
import core.basesyntax.userservice.repository.RoleRepository;
import core.basesyntax.userservice.repository.UserRepository;
import core.basesyntax.userservice.security.JwtUtil;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public static final String USER_CREATED_ROUTING_KEY = "user.created";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;

    public UserResponseDto registerNewUser(CreateUserRequestDto requestDto) {
        User user = userMapper.toModel(requestDto);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RegistrationException("User with email "
                    + user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(requestDto.password()));
        Role userRole = roleRepository.findByName(Role.RoleName.USER).orElseThrow(() ->
                new EntityNotFoundException("Can't find role by name. Name: "
                        + Role.RoleName.USER));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_NAME,
                USER_CREATED_ROUTING_KEY, userMapper.modelToCreatedEvent(user));
        return userMapper.toDto(user);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestDto.email(), requestDto.password()));
        String token = jwtUtil.generateToken(authentication.getName());
        return new LoginResponseDto(token);
    }
}
