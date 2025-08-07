package com.app.focus.dto.user;

import com.app.focus.entity.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Set<String> roles;
}
