package com.app.focus.service;

import com.app.focus.dto.user.UserRequestDTO;
import com.app.focus.dto.user.UserResponseDTO;
import com.app.focus.entity.Role;
import com.app.focus.entity.User;
import com.app.focus.entity.enums.UserStatus;
import com.app.focus.interfaces.IUserService;
import com.app.focus.repository.RoleRepository;
import com.app.focus.repository.UserRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authUserProvider;

    @Override
    public List<UserResponseDTO> getUsersFiltered(LocalDateTime startDate, LocalDateTime endDate, String status) {
        log.info("Fetching users with filters - StartDate: {}, EndDate: {}, Status: {}", startDate, endDate, status);

        UserStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status. Use: ACTIVE, BLOCKED, DELETED.");
            }
        }

        List<User> users = userRepository.findAllWithFilters(startDate, endDate, statusEnum);
        log.info("Retrieved {} user(s) matching filters", users.size());

        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO dto) {
        log.info("Request to update user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            log.debug("Updating email to '{}'", dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        if (dto.getFirstName() != null) {
            log.debug("Updating firstName to '{}'", dto.getFirstName());
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            log.debug("Updating lastName to '{}'", dto.getLastName());
            user.setLastName(dto.getLastName());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            log.debug("Updating password");
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            log.debug("Updating roles: {}", dto.getRoles());
            Set<Role> roles = dto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new EntityNotFoundException("Role '" + roleName + "' not found")))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        if (dto.getStatus() != null) {
            log.debug("Updating status to '{}'", dto.getStatus());
            user.setStatus(dto.getStatus());
        }

        User updated = userRepository.save(user);
        log.info("User ID {} updated successfully", updated.getId());

        return toResponseDTO(updated);
    }


    private UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public UserResponseDTO getCurrentUserProfile() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Retrieving profile for user: {}", user.getEmail());
        return toResponseDTO(user);
    }
}
