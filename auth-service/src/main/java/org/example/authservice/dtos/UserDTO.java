package org.example.authservice.dtos;


import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;


    public UserDTO(Long id, String username, String email, String s) {
    }
}
