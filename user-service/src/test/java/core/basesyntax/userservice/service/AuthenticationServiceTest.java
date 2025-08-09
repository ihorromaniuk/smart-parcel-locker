package core.basesyntax.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthenticationServiceTest {
    private static final String USER_CREATED_ROUTING_KEY = "user.created";

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterNewUserWithValidData() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "test@example.com",
                "password123",
                "password123",
                "Test User");
        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("hashedPassword");
        Role userRole = new Role();
        userRole.setName(Role.RoleName.USER);
        user.setRoles(Set.of(userRole));
        UserResponseDto responseDto = new UserResponseDto(1L,
                "test@example.com",
                "Test User",
                Set.of(userRole),
                true);

        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.modelToCreatedEvent(user)).thenReturn(null);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = authenticationService.registerNewUser(requestDto);

        assertEquals(responseDto, result);
        verify(userRepository).save(user);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.TOPIC_EXCHANGE_NAME),
                eq(USER_CREATED_ROUTING_KEY),
                Optional.ofNullable(any()));
    }

    @Test
    void shouldAssignUserRoleOnRegistration() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "roleuser@example.com",
                "password123",
                "password123",
                "Role User");
        User user = new User();
        user.setEmail("roleuser@example.com");
        user.setFullName("Role User");
        user.setPassword("hashedPassword");
        Role userRole = new Role();
        userRole.setName(Role.RoleName.USER);

        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.existsByEmail("roleuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.modelToCreatedEvent(user)).thenReturn(null);
        when(userMapper.toDto(user)).thenReturn(
                new UserResponseDto(2L,
                        "roleuser@example.com",
                        "Role User",
                        Set.of(userRole),
                        true));

        authenticationService.registerNewUser(requestDto);

        assertTrue(user.getRoles().contains(userRole));
        assertEquals(1, user.getRoles().size());
        assertEquals(Role.RoleName.USER, userRole.getName());
    }

    @Test
    void shouldLoginWithValidCredentialsAndReturnToken() {
        LoginRequestDto requestDto = new LoginRequestDto("login@example.com",
                "password123");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("login@example.com");
        when(jwtUtil.generateToken("login@example.com")).thenReturn("jwt-token");

        LoginResponseDto response = authenticationService.login(requestDto);

        assertEquals("jwt-token", response.token());
        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("login@example.com");
    }

    @Test
    void shouldThrowRegistrationExceptionForDuplicateEmail() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "duplicate@example.com",
                "password123",
                "password123",
                "Duplicate User");
        User user = new User();
        user.setEmail("duplicate@example.com");

        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> authenticationService.registerNewUser(requestDto)
        );
        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfUserRoleMissing() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "norole@example.com",
                "password123",
                "password123",
                "No Role User");
        User user = new User();
        user.setEmail("norole@example.com");

        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.existsByEmail("norole@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> authenticationService.registerNewUser(requestDto)
        );
        assertTrue(exception.getMessage().contains("Can't find role by name"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotLoginWithInvalidCredentials() {
        LoginRequestDto requestDto = new LoginRequestDto("invalid@example.com",
                "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(requestDto));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}