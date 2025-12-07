package org.example.authservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.authservice.enums.Role;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("USER")
public class NormalUser extends User {

    public NormalUser() {
        setRole(Role.USER);
    }


    @Override
    public String getRoleName() {
        // 🔹 Si le rôle est nul (par ex. pas encore défini), retourne "USER" par défaut
        return getRole() != null ? getRole().name() : Role.USER.name();
    }
}
