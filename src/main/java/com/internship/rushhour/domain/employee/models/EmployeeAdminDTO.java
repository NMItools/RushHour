package com.internship.rushhour.domain.employee.models;

import com.internship.rushhour.domain.provider.models.ProviderDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record EmployeeAdminDTO(
        @Valid
        @NotNull(message = "Please include employee details")
        EmployeeDTO employee,
        @NotNull(message = "Provider can not be null")
        @Valid
        ProviderDTO provider
) {
}
