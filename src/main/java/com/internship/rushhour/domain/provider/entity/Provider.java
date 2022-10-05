package com.internship.rushhour.domain.provider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Entity
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can not be blank")
    @Column(unique=true)
    @Size(min=3, message ="Name can not be shorter than three characters")
    private String name;

    @NotBlank
    @Pattern(regexp = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)",
            message = "Must be a valid URL")
    private String website;

    @NotBlank(message = "Domain can not be blank")
    @Size(min=2, message = "Domain can not be shorter than two characters")
    @Pattern(regexp = "[a-zA-Z]+", message = "Invalid characters in domain")
    @Column(unique = true)
    private String businessDomain;

    @NotBlank(message="Phone can not be blank")
    @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
            message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Business hour end can not be blank")
    @JsonFormat(pattern = "HH:mm")
    LocalTime businessHoursStart;

    @NotNull(message = "Business hour start can not be blank")
    @JsonFormat(pattern = "HH:mm")
    LocalTime businessHoursEnd;

    @ElementCollection(targetClass = DayOfWeek.class)
    @Column
    @Enumerated(EnumType.STRING)
    Set<DayOfWeek> workingDays;

    public Provider() {}

    public Provider(Long id, String name, String website, String businessDomain, String phone, LocalTime businessHoursStart, LocalTime businessHoursEnd, Set<DayOfWeek> workingDays) {
        this.id = id;
        this.name = name;
        this.website = website;
        this.businessDomain = businessDomain;
        this.phone = phone;
        this.businessHoursStart = businessHoursStart;
        this.businessHoursEnd = businessHoursEnd;
        this.workingDays = workingDays;
    }

    public LocalTime getBusinessHoursStart() {
        return businessHoursStart;
    }

    public void setBusinessHoursStart(LocalTime businessHoursStart) {
        this.businessHoursStart = businessHoursStart;
    }

    public LocalTime getBusinessHoursEnd() {
        return businessHoursEnd;
    }

    public void setBusinessHoursEnd(LocalTime businessHoursEnd) {
        this.businessHoursEnd = businessHoursEnd;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<DayOfWeek> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Set<DayOfWeek> workingDays) {
        this.workingDays = workingDays;
    }
}
