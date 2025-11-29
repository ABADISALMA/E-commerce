package org.example.userservice;

import org.example.userservice.entities.User;
import org.example.userservice.enums.RoleType;
import org.example.userservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;  // ⚠️ à ajouter

import java.time.LocalDateTime;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) { // ⚠️ injection ici
        return args -> {
            // Nettoyage de la base
            userRepository.deleteAll();

            // Ajout d'utilisateurs avec mots de passe HASHÉS
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))  // ✅ hash
                    .role(RoleType.ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();

            User user1 = User.builder()
                    .username("yassine")
                    .email("yassine@example.com")
                    .password(passwordEncoder.encode("user123"))   // ✅ hash
                    .role(RoleType.CUSTOMER)
                    .createdAt(LocalDateTime.now())
                    .build();

            User user2 = User.builder()
                    .username("sara")
                    .email("sara@example.com")
                    .password(passwordEncoder.encode("pass456"))   // ✅ hash
                    .role(RoleType.CUSTOMER)
                    .createdAt(LocalDateTime.now())
                    .build();

            User user3 = User.builder()
                    .username("mehdi")
                    .email("mehdi@example.com")
                    .password(passwordEncoder.encode("mehdi789"))  // ✅ hash
                    .role(RoleType.CUSTOMER)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            userRepository.findAll().forEach(u ->
                    System.out.println("==> USER : " + u.getUsername() + " | " + u.getEmail())
            );
        };
    }
}
