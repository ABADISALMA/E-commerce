package org.example.userservice.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}