package org.example.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.authservice.Services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // ✅ Extract JWT token from Authorization header
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // ✅ Extract username from token
                String username = jwtUtil.extractUsername(token);

                // ✅ If username exists and no authentication is set yet
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // ✅ Load user details
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    // ✅ Validate token
                    if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                        // ✅ Create authentication token with authorities
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities() // ✅ CRITICAL: Include authorities
                                );

                        // ✅ Set additional details
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // ✅ Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        System.out.println("✅ User authenticated: " + username + " with roles: " + userDetails.getAuthorities());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ JWT Filter error: " + e.getMessage());
            // Don't throw exception, just continue the filter chain
        }

        // ✅ Continue filter chain
        filterChain.doFilter(request, response);
    }
}