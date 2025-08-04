package core.basesyntax.userservice.controller;

import core.basesyntax.userservice.dto.user.CreateUserRequestDto;
import core.basesyntax.userservice.dto.user.LoginRequestDto;
import core.basesyntax.userservice.dto.user.LoginResponseDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public UserResponseDto createNewUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        return userService.registerNewUser(requestDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        return userService.login(requestDto);
    }
}
