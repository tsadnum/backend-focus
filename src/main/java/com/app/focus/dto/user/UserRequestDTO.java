package com.app.focus.dto.user;

import com.app.focus.entity.enums.UserStatus;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private UserStatus status;
}
