package org.example.authservice.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import org.example.authservice.enums.Role;

@Entity
@Data
@DiscriminatorValue("SUPERADMIN")
public class SuperAdmin extends User {

    public SuperAdmin() {
        setRole(Role.SUPERADMIN);
    }
    @Override
    public String getRoleName() {
        return getRole() != null ? getRole().name() : Role.SUPERADMIN.name();
    }
}
