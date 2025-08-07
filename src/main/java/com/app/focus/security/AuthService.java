package com.app.focus.security;

import com.app.focus.dto.auth.AuthRequest;
import com.app.focus.dto.auth.AuthResponse;
import com.app.focus.dto.auth.RegisterRequest;
import com.app.focus.entity.Role;
import com.app.focus.entity.User;
import com.app.focus.entity.enums.UserStatus;
import com.app.focus.repository.RoleRepository;
import com.app.focus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        var defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("Default role 'ROLE_USER' not found in the database");
                    return new IllegalStateException("Default role not found");
                });

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(defaultRole))
                .status(UserStatus.ACTIVE)
                .build();
        log.debug("Persisting new user: {}", user.getEmail());
        userRepository.save(user);
        log.info("User registered successfully: {}", request.getEmail());

        String jwt = jwtService.generateToken(user);
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        log.debug("Assigned roles to {}: {}", user.getEmail(), roleNames);

        return new AuthResponse(jwt, roleNames);
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Attempting login for: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", request.getEmail());
                    return new UsernameNotFoundException("User not found");
                });

        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        log.info("Login successful for: {}", request.getEmail());

        String jwt = jwtService.generateToken(user);
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        log.debug("User {} has roles: {}", user.getEmail(), roleNames);

        return new AuthResponse(jwt, roleNames);
    }

}

