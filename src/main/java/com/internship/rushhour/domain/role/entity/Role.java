package com.internship.rushhour.domain.role.entity;

import com.internship.rushhour.domain.account.entity.Account;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Role name can not be blank")
    @Column(unique=true)
    @Size(min=3, message="Role name needs to be at least 3 letters long")
    String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    List<Account> account;

    public Role() {}

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

