package com.app.focus.security;

import com.app.focus.dto.auth.AuthRequest;
import com.app.focus.dto.auth.AuthResponse;
import com.app.focus.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());

        AuthResponse response = authService.register(request);

        log.info("User registered: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        log.info("Login success for: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
}

