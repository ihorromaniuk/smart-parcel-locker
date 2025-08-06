package core.basesyntax.userservice.controller;

import core.basesyntax.userservice.dto.user.CreateUserRequestDto;
import core.basesyntax.userservice.dto.user.LoginRequestDto;
import core.basesyntax.userservice.dto.user.LoginResponseDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public UserResponseDto createNewUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        return authenticationService.registerNewUser(requestDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }
}
