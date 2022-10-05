package com.internship.rushhour.domain.appointment.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record AppointmentDTO(
        @NotNull(message = "Start date required")
        @JsonFormat(pattern = "yyyy-MM-dd@HH:mm")
        LocalDateTime startTime,
        @NotNull(message = "An employee must be selected")
        Long employeeId,
        @NotNull(message = "A client must be selected")
        Long clientId,
        @Size(min=1, message = "At least one activity required for the appointment")
        List<Long> activities,
        boolean clientWantsGoogleCalendar,
        boolean employeeWantsGoogleCalendar,
        boolean clientWantsMicrosoftCalendar,
        boolean employeeWantsMicrosoftCalendar
) {
}
