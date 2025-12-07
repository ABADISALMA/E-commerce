package org.example.authservice.security;

import org.example.authservice.entities.Admin;
import org.example.authservice.entities.NormalUser;
import org.example.authservice.enums.Role;
import org.example.authservice.entities.SuperAdmin;
import org.example.authservice.Repositories.UserRepository;
import org.example.authservice.Services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * ✅ Création d'utilisateurs au démarrage (si inexistants)
     */
    @Bean
    CommandLineRunner initUsers(UserService userService, UserRepository repo) {
        return args -> {
            // ✅ SuperAdmin
            if (repo.findByUsername("supadmin").isEmpty()) {
                SuperAdmin superAdmin = new SuperAdmin();
                superAdmin.setUsername("supadmin");
                superAdmin.setPassword("admin123");
                superAdmin.setRole(Role.SUPERADMIN);
                superAdmin.setEmail("supadmin@gmail.com");
                userService.save(superAdmin);
                System.out.println("✅ Super admin créé : supadmin / admin123");
            }

            // ✅ User normal
            if (repo.findByUsername("user").isEmpty()) {
                NormalUser user = new NormalUser();
                user.setUsername("user");
                user.setPassword("123456");
                user.setRole(Role.USER);
                user.setEmail("user@gmail.com");
                userService.save(user);
                System.out.println("✅ Utilisateur créé : user / 123456");
            }

            // ✅ Admin
            if (repo.findByUsername("admin").isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("123456");
                admin.setRole(Role.ADMIN);
                admin.setEmail("ayman@gmail.com");
                userService.save(admin);
                System.out.println("✅ Admin créé : admin / 123456");
            }
        };
    }

    /**
     * ✅ Configuration principale Spring Security
     * FIXED: Changed hasRole to hasAuthority to match ROLE_ prefix handling
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        // ✅ FIX: Use hasAuthority with ROLE_ prefix or ensure User entity returns correct authorities
                        .requestMatchers("/superadmin/**").hasAuthority("ROLE_SUPERADMIN")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ Autorise Angular (localhost:4200)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * ✅ AuthManager pour login()
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * ✅ CRITICAL: PasswordEncoder Bean pour Spring Security
     * Sans ce bean, Spring Security ne sait pas comment vérifier les mots de passe
     */
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}