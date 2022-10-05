package com.internship.rushhour.domain.provider.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.Set;

public record ProviderDTO(
        @NotBlank(message = "Name can not be blank")
        @Size(min=3, message ="Name can not be shorter than three characters")
        String name,

        @NotBlank
        @Pattern(regexp = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)",
                message = "Must be a valid URL")
        String website,

        @NotBlank(message = "Domain can not be blank")
        @Size(min=2, message = "Domain can not be shorter than two characters")
        @Pattern(regexp = "[a-zA-Z]+", message = "Invalid characters in domain")
        String businessDomain,

        @NotBlank(message="Phone can not be blank")
        @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
                message = "Invalid phone number")
        String phone,

        @NotNull(message = "Business hour start can not be blank")
        @JsonFormat(pattern = "HH:mm")
        LocalTime businessHoursStart,

        @NotNull(message = "Business hour end can not be blank")
        @JsonFormat(pattern = "HH:mm")
        LocalTime businessHoursEnd,

        Set<@Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY", message = "all days must be in capital letters and spelled correctly")
                String> workingDays)
{
}
