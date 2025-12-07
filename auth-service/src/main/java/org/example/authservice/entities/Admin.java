package org.example.authservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.authservice.enums.Role;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ADMIN")

public class Admin extends User {

public Admin(){
    setRole(Role.ADMIN);
}




    @Override
    public String getRoleName() {
        return getRole() != null ? getRole().name() : Role.ADMIN.name();
    }
}
