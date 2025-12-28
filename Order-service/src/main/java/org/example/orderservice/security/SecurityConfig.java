package org.example.orderservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity // ✅ Ajouter ceci

public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ POST - Créer une commande : USER, ADMIN, SUPERADMIN
                        .requestMatchers(HttpMethod.POST, "/orders/add")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // ✅ GET - Consulter ses commandes : USER, ADMIN, SUPERADMIN
                        .requestMatchers(HttpMethod.GET, "/orders/by-user/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/orders/by-id/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/orders/tracking/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // ✅ GET - Voir toutes les commandes : ADMIN, SUPERADMIN seulement
                        .requestMatchers(HttpMethod.GET, "/orders/all")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // ✅ PUT - Modifier le statut : USER, ADMIN, SUPERADMIN
                        .requestMatchers(HttpMethod.PUT, "/orders/update-status/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // ✅ DELETE - Supprimer : ADMIN, SUPERADMIN seulement
                        .requestMatchers(HttpMethod.DELETE, "/orders/delete/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}