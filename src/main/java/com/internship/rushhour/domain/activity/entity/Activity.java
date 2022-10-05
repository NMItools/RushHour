package com.internship.rushhour.domain.activity.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.infrastructure.serializers.ActivityEmployeeSerializer;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.Set;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Positive(message = "Price must be greater than zero")
    float price;

    @NotNull
    @DurationMin(minutes = 1, message = "Duration must be at least 1 minute long")
    Duration duration;

    @ManyToOne
    @JoinColumn(name = "provider")
    @NotNull
    Provider provider;

    @ManyToMany
    @JoinTable(
            name = "activity_employees",
            joinColumns = @JoinColumn(name="activity_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @JsonSerialize(contentUsing = ActivityEmployeeSerializer.class)
    Set<Employee> employees;

    @Size(min = 2 ,message = "Activity name must be at least 2 characters long")
    String name;

    public Activity() {}

    public Activity(Long id, float price, Duration duration, Provider provider, Set<Employee> employees, String name) {
        this.id = id;
        this.price = price;
        this.duration = duration;
        this.provider = provider;
        this.employees = employees;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
