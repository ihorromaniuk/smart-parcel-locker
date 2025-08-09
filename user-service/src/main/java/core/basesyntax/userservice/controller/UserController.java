package core.basesyntax.userservice.controller;

import core.basesyntax.userservice.dto.user.DisableUserRequestDto;
import core.basesyntax.userservice.dto.user.EnableUserRequestDto;
import core.basesyntax.userservice.dto.user.RestoreUserRequestDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PatchMapping("/update-user")
    public ResponseEntity<UserResponseDto> updateUserInfo(
            @RequestBody @Valid UpdateUserInfoRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserInfo(requestDto, user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @RequestBody @Valid UpdateUserRoleRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserRole(requestDto, user));
    }

    @PatchMapping("/update-email")
    public ResponseEntity<UserResponseDto> updateUserEmail(
            @RequestBody @Valid UpdateUserEmailRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserEmail(requestDto, user));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<UserResponseDto> updateUserPassword(
            @RequestBody @Valid UpdateUserPasswordRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserPassword(requestDto, user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getInfoAboutCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getInfoAboutUser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/disable")
    public ResponseEntity<UserResponseDto> disableUserByEmail(@Valid
                                                       @RequestBody
                                                       DisableUserRequestDto requestDto,
                                                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.disableUser(requestDto, user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/enable")
    public ResponseEntity<UserResponseDto> enableUserByEmail(@Valid
                                                       @RequestBody
                                                       EnableUserRequestDto requestDto,
                                                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.enableUser(requestDto, user));
    }

    @PreAuthorize("hasAnyRole('USER', 'COURIER')")
    @DeleteMapping
    public ResponseEntity<?> deleteCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.deleteUserById(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/restore")
    public ResponseEntity<UserResponseDto> restoreUserByEmail(@Valid
                                                              @RequestBody
                                                              RestoreUserRequestDto requestDto) {
        return ResponseEntity.ok(userService.restoreUserByEmail(requestDto));
    }

}
