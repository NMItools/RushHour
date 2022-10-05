package com.internship.rushhour.domain.appointment.models;

import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;

import java.time.LocalDateTime;
import java.util.List;

public record AppointmentResponseDTO(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endDate,
        EmployeeDTOResponse employee,
        ClientDTO client,
        float price,
        List<ActivityResponseDTO> activities
) {
}
