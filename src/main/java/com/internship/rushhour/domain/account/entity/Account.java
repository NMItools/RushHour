package com.internship.rushhour.domain.account.entity;

import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.infrastructure.validators.EncryptedPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message="Email required")
    @Email(message = "Must be a valid email address")
    @Column(unique = true)
    String email;

    @NotBlank(message="Name cannot be blank")
    @Size(min=3, message="Name must be at least 3 characters long")
    @Pattern(regexp = "^[a-zA-Z](?:[ '.\\-a-zA-Z]*[a-zA-Z])?[']?$", message = "Name can only contain letters, hyphens and apostrophes")
    String name;

    @NotBlank
    @EncryptedPassword
    String password;

    @ManyToOne
    @JoinColumn(name="account_role")
    @NotNull(message = "A role must be selected")
    Role role;

    public Account(){}

    public Account(Long id, String email, String name, String password, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
