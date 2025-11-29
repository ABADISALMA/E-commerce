package org.example.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token; // pour l'instant on met une chaîne, plus tard ce sera un JWT
}