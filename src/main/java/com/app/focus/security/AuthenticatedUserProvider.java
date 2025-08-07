package com.app.focus.security;

import com.app.focus.entity.User;
import com.app.focus.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    @Transactional
    public User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("No valid authentication found in SecurityContext");
            throw new UsernameNotFoundException("User not authenticated");
        }

        String email = authentication.getName();
        log.debug("Authentication found. Extracted email: {}", email);

        return userRepository.findByEmail(email)
                .map(user -> {
                    log.info("Authenticated user found: {}", user.getEmail());
                    return user;
                })
                .orElseThrow(() -> {
                    log.warn("Authenticated user not found in database: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }
}
