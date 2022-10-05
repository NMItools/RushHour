package com.internship.rushhour.domain.account.models;

import com.internship.rushhour.infrastructure.validators.ValidPassword;

import javax.persistence.Column;
import javax.validation.constraints.*;

public class AccountDTO {
    @NotBlank(message="Email required")
    @Email(message = "Must be a valid email address")
    @Column(unique = true)
    String email;

    @Size(min=3, message="Name must be at least 3 characters long")
    @Pattern(regexp = "^[a-zA-Z](?:[ '.\\-a-zA-Z]*[a-zA-Z])?[']?$", message = "Name can only contain letters, hyphens and apostrophes")
    String name;

    @ValidPassword
    String password;

    @NotNull(message = "A role must be selected")
    Long roleId;

    public AccountDTO(String email, String name, String password, Long roleId) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.roleId = roleId;
    }

    public AccountDTO(){}

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long id) {
        this.roleId = id;
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

}
