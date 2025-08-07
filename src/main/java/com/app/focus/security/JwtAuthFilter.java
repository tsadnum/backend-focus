package com.app.focus.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String requestPath = request.getRequestURI();

        log.debug("Incoming request to [{}] with Authorization header: {}", requestPath, authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid JWT token found in Authorization header for path: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(token);
            if (username == null) {
                log.warn("Failed to extract username from JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Extracted username from token: {}", username);

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("SecurityContext already has authentication for user: {}", username);
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                log.info("JWT token is valid for user: {}", username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("SecurityContext updated with authenticated user: {}", username);
            } else {
                log.warn("JWT token is invalid for user: {}", username);
            }

        } catch (Exception ex) {
            log.error("Exception while processing JWT on path [{}]: {}", requestPath, ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}
