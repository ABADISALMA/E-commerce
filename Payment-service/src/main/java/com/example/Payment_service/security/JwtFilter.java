package com.example.Payment_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("🔐 JwtFilter - Requête reçue: " + request.getMethod() + " " + request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("🔑 Token détecté: " + token.substring(0, Math.min(20, token.length())) + "...");

            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);
                    Long userId = jwtUtil.extractUserId(token);

                    System.out.println("✅ Token valide:");
                    System.out.println("   - Username: " + username);
                    System.out.println("   - Role: " + role);
                    System.out.println("   - User ID: " + userId);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority(role))
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("✅ Authentication définie dans SecurityContext");
                } else {
                    System.out.println("❌ Token invalide ou expiré");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de la validation du token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Aucun token Authorization dans les headers");
        }

        filterChain.doFilter(request, response);
    }
}