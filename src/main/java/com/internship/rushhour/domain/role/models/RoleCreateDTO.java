package com.internship.rushhour.domain.role.models;

import javax.validation.constraints.Size;

public class RoleCreateDTO {
    @Size(min=3, message = "Role name can not be shorter than 3 characters")
    private String roleName;

    public RoleCreateDTO() {}

    public RoleCreateDTO(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
