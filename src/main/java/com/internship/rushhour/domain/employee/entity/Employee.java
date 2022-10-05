package com.internship.rushhour.domain.employee.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.provider.entity.Provider;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message="Phone can not be blank")
    @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
            message = "Invalid phone number")
    String phone;

    @ManyToOne
    @JoinColumn(name = "provider")
    @NotNull(message = "Provider can not be null")
    Provider provider;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @JoinColumn(name="account", unique = true)
    @NotNull(message = "Account can not be null")
    Account account;

    @NotNull(message = "Rate per hour must be set")
    @Positive(message = "Rate per hour must be positive")
    float ratePerHour;

    @NotBlank(message = "Title required")
    @Size(min=2, message = "Title must be at least 2 characters long")
    String title;

    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm")
    LocalDateTime hireDate;

    public Employee() {}

    public Employee(Long id, String phone, Provider provider, Account account, float ratePerHour, String title, LocalDateTime hireDate) {
        this.id = id;
        this.phone = phone;
        this.provider = provider;
        this.account = account;
        this.ratePerHour = ratePerHour;
        this.title = title;
        this.hireDate = hireDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public float getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(float ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }
}

