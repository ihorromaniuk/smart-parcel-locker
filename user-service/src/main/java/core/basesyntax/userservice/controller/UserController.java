package core.basesyntax.userservice.controller;

import core.basesyntax.userservice.dto.user.UpdateUserEmailRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserInfoRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserPasswordRequestDto;
import core.basesyntax.userservice.dto.user.UpdateUserRoleRequestDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.model.User;
import core.basesyntax.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('USER', 'COURIER')")
    @PatchMapping("/update-user")
    ResponseEntity<UserResponseDto> updateUserInfo(
            @RequestBody @Valid UpdateUserInfoRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserInfo(requestDto, user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-role")
    ResponseEntity<UserResponseDto> updateUserRole(
            @RequestBody @Valid UpdateUserRoleRequestDto requestDto
    ) {
        return ResponseEntity.ok(userService.updateUserRole(requestDto));
    }

    @PatchMapping("/update-email")
    ResponseEntity<UserResponseDto> updateUserEmail(
            @RequestBody @Valid UpdateUserEmailRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserEmail(requestDto, user));
    }

    @PatchMapping("/update-password")
    ResponseEntity<UserResponseDto> updateUserPassword(
            @RequestBody @Valid UpdateUserPasswordRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserPassword(requestDto, user));
    }
}
