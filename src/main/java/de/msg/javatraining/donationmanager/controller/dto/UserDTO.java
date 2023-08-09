package de.msg.javatraining.donationmanager.controller.dto;

import de.msg.javatraining.donationmanager.persistence.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
}
