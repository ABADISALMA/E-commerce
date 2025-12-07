package org.example.authservice.Services;

import org.example.authservice.Repositories.UserRepository;
import org.example.authservice.dtos.UserDTO;
import org.example.authservice.entities.Admin;
import org.example.authservice.entities.NormalUser;
import org.example.authservice.entities.SuperAdmin;
import org.example.authservice.entities.User;
import org.example.authservice.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // ✅ Enregistrement d'un nouvel utilisateur avec rôle et email
    @Transactional
    public User register(String username, String password, String role, String email) {
        if (repo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("❌ Username already exists");
        }
        if (repo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("❌ Email already exists");
        }

        // ✅ Rôle par défaut : USER
        if (role == null || role.isBlank()) {
            role = "USER";
        }

        User user;
        switch (role.toUpperCase()) {
            case "ADMIN" -> {
                user = new Admin();
                user.setRole(Role.ADMIN);
            }
            case "SUPERADMIN" -> {
                user = new SuperAdmin();
                user.setRole(Role.SUPERADMIN);
            }
            default -> {
                user = new NormalUser();
                user.setRole(Role.USER);
            }
        }

        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setEmail(email);

        return repo.save(user);
    }

    // ✅ Sauvegarde d'un utilisateur (FIXED: encode only if not already encoded)
    @Transactional
    public User save(User user) {
        // S'assurer que le rôle est défini
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // ✅ FIX: Only encode if password is not already encoded (BCrypt hash starts with $2a$, $2b$, or $2y$)
        String password = user.getPassword();
        if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
            user.setPassword(encoder.encode(password));
        }

        return repo.save(user);
    }

    // ✅ Recherche utilisateur par username
    public User findByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Vérifier un mot de passe brut/encodé
    public boolean checkPassword(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }

    // ✅ Implémentation Spring Security (FIXED: better error handling)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // ✅ Ensure user has proper authorities set
        return user; // Ton entité User implémente UserDetails
    }

    // ✅ Récupérer tous les utilisateurs en DTO
    public List<UserDTO> getAllUsersAsDto() {
        return repo.findAll().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole() != null ? user.getRole().name() : "UNKNOWN"
                ))
                .collect(Collectors.toList());
    }

    // ✅ Modifier le rôle d'un utilisateur
    @Transactional
    public String updateUserRole(Long id, String role) {
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validation du rôle reçu
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Invalid role");
        }

        Role newRole = Role.valueOf(role.toUpperCase());
        user.setRole(newRole);
        repo.save(user);

        return "✅ Role updated successfully for user: " + user.getUsername();
    }
}