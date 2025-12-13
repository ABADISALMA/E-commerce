package org.example.authservice.controller;

import org.example.authservice.Services.UserService;
import org.example.authservice.dtos.LoginRequest;
import org.example.authservice.dtos.RegisterRequest;
import org.example.authservice.entities.User;
import org.example.authservice.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    /**
     * ✅ REGISTER - Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(
                    request.getUsername(),
                    request.getPassword(),
                    request.getRole(),
                    request.getEmail()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "✅ User registered successfully");
            response.put("username", user.getUsername());
            response.put("role", user.getRole().name());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ✅ LOGIN - Authentification et génération du JWT (WITH DEBUG)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // ✅ LOG 1: Check what we received
            System.out.println("🔍 Login attempt for user: " + request.getUsername());

            // ✅ LOG 2: Check if user exists in database
            User user = null;
            try {
                user = userService.findByUsername(request.getUsername());
                System.out.println("✅ User found in database: " + user.getUsername());
                System.out.println("🔍 User role: " + user.getRole());
                System.out.println("🔍 Password in DB starts with: " + user.getPassword().substring(0, 10) + "...");
            } catch (Exception e) {
                System.err.println("❌ User not found in database: " + request.getUsername());
                return ResponseEntity.status(401)
                        .body(Map.of("error", "❌ User not found"));
            }

            // ✅ LOG 3: Attempt authentication
            System.out.println("🔍 Attempting Spring Security authentication...");

            try {
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );
                System.out.println("✅ Authentication successful!");
            } catch (BadCredentialsException e) {
                System.err.println("❌ Bad credentials: " + e.getMessage());

                // ✅ Manual password check for debugging
                boolean manualCheck = userService.checkPassword(request.getPassword(), user.getPassword());
                System.out.println("🔍 Manual password check result: " + manualCheck);

                return ResponseEntity.status(401)
                        .body(Map.of("error", "❌ Invalid password"));
            }

            // ✅ Génération du token JWT
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().name(),
                    user.getEmail(),
                    user.getId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("role", user.getRole().name());
            response.put("email", user.getEmail());

            System.out.println("✅ Login successful, token generated");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401)
                    .body(Map.of("error", "❌ Invalid credentials: " + e.getMessage()));
        }
    }

    /**
     * ✅ PROFILE - Récupère les infos de l'utilisateur connecté
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "❌ No authentication found (JWT manquant ou invalide)"));
            }

            // Ici on ne caste plus en UserDetails directement
            Object principal = authentication.getPrincipal();

            String username;

            if (principal instanceof UserDetails userDetails) {
                username = userDetails.getUsername();
            } else {
                // Dans ta config actuelle, ce sera un String => ok
                username = principal.toString();
            }

            // TODO: récupérer ton user depuis la BDD si tu veux plus d'infos
            // Exemple :
            // UserEntity user = userRepository.findByUsername(username);

            Map<String, Object> body = new HashMap<>();
            body.put("username", username);
            body.put("message", "Profil récupéré avec succès via JWT");
            // body.put("roles", authentication.getAuthorities()); // si tu veux ajouter les rôles

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ Error retrieving profile: " + e.getMessage()));
        }
    }

    /**
     * 🔍 DEBUG ENDPOINT - Check user password encoding
     */
    @GetMapping("/debug/user/{username}")
    public ResponseEntity<?> debugUser(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            Map<String, Object> debug = new HashMap<>();
            debug.put("username", user.getUsername());
            debug.put("email", user.getEmail());
            debug.put("role", user.getRole().name());
            debug.put("passwordPrefix", user.getPassword().substring(0, 10));
            debug.put("passwordLength", user.getPassword().length());
            debug.put("isBCrypt", user.getPassword().startsWith("$2"));
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}