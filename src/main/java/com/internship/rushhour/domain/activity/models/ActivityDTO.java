package com.internship.rushhour.domain.activity.models;

import org.hibernate.validator.constraints.time.DurationMin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.List;

public record ActivityDTO (
        @NotNull(message = "Price can not be null")
        @Positive
        float price,

        @NotNull(message = "Duration can not be null")
        @DurationMin(minutes = 1, message = "Duration must be at least 1 minute long")
        Duration duration,

        @NotNull(message = "A provider must be selected")
        Long providerId,

        @NotNull(message = "At least one employee must be selected")
        List<Long> employeeIds,

        @Size(min = 2 ,message = "Activity name must be at least 2 characters long")
        @NotBlank(message = "Please chose a name for the activity")
        String name
){
}
