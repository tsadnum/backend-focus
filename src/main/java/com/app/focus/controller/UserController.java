
package com.app.focus.controller;

import com.app.focus.dto.user.UserRequestDTO;
import com.app.focus.dto.user.UserResponseDTO;
import com.app.focus.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    @GetMapping("/api/user/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        log.info("GET /api/user/profile - Retrieving current user profile");
        UserResponseDTO profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> listUsers(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String status
    ) {
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        if (startDateTime != null || endDateTime != null || (status != null && !status.isBlank())) {
            log.info("Admin is retrieving users with filters - Start Date: {}, End Date: {}, Status: {}",
                    startDateTime, endDateTime, status);
        } else {
            log.info("Admin is retrieving all users without filters.");
        }

        List<UserResponseDTO> users = userService.getUsersFiltered(startDateTime, endDateTime, status);
        log.info("Returned {} user(s) in response.", users.size());
        return users;
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserRequestDTO dto
    ) {
        log.info("PUT /admin/users/{} - Payload: {}", id, dto);
        UserResponseDTO updatedUser = userService.updateUser(id, dto);
        log.info("User ID {} updated successfully.", id);
        return ResponseEntity.ok(updatedUser);
    }
}