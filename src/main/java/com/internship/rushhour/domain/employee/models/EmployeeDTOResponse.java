package com.internship.rushhour.domain.employee.models;

import java.time.LocalDateTime;

public record EmployeeDTOResponse(
        Long id,
        String phone,
        String providerName,
        String name,
        String email,
        float ratePerHour,
        String title,
        LocalDateTime hireDate)
{
}
