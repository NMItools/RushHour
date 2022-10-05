package com.internship.rushhour.domain.employee.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.internship.rushhour.domain.account.models.AccountDTO;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

public record EmployeeDTO (
        @NotBlank(message="Phone can not be blank")
        @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
                message = "Invalid phone number")
        String phone,

        @NotNull(message = "Provider can not be null")
        Long providerId,

        @NotNull(message = "Account can not be null")
        @Valid
        AccountDTO accountDTO,

        @NotNull(message = "Rate per hour must be set")
        @Positive(message = "Rate per hour must be positive")
        float ratePerHour,

        @NotBlank(message = "Title required")
        @Size(min=2, message = "Title must be at least 2 characters long")
        String title,

        @JsonFormat(pattern = "yyyy-MM-dd@HH:mm")
        LocalDateTime hireDate
){
}

