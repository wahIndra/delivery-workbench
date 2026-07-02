package com.deliveryworkbench.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * MockAuthFilter — DEVELOPMENT ONLY (active only on "dev" and "test" Spring profiles).
 *
 * <p>Reads {@code X-Mock-Username} and {@code X-Mock-Role} HTTP headers sent by the
 * React frontend during local development (see {@code frontend/src/api.js}) and
 * creates an authenticated Spring Security context from them. This allows
 * {@link SecurityUtils#getCurrentUsername()} to return the correct username
 * instead of {@code null}, so AI audit logs and stage history are attributed correctly.
 *
 * <p><strong>MUST NEVER be active in production.</strong>
 * The {@code @Profile} annotation ensures this. In production, a real JWT filter replaces this.
 */
@Slf4j
@Component
@Profile({"dev", "test"})
public class MockAuthFilter extends OncePerRequestFilter {

    private static final String USERNAME_HEADER = "X-Mock-Username";
    private static final String ROLE_HEADER     = "X-Mock-Role";
    private static final String DEFAULT_USER    = "anonymous";
    private static final String DEFAULT_ROLE    = "BUSINESS_USER";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Skip if already authenticated (e.g., by a real JWT filter in future)
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getHeader(USERNAME_HEADER);
        String role     = request.getHeader(ROLE_HEADER);

        if (username == null || username.isBlank()) {
            username = DEFAULT_USER;
        }
        if (role == null || role.isBlank()) {
            role = DEFAULT_ROLE;
        }

        // Sanitize: role must be an alphanumeric+underscore string
        if (!role.matches("[A-Z_]+")) {
            log.warn("[MockAuth] Rejected invalid role header: {}", role);
            role = DEFAULT_ROLE;
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
        );

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("[MockAuth] Authenticated request as user={} role={}", username, role);

        filterChain.doFilter(request, response);
    }
}
