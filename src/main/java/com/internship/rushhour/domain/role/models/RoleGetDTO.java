package com.internship.rushhour.domain.role.models;

public class RoleGetDTO {
    private Long id;
    private String roleName;

    public RoleGetDTO() {}

    public RoleGetDTO(Long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
