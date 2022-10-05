package com.internship.rushhour.domain.activity.models;

import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.provider.models.ProviderDTO;

import java.time.Duration;
import java.util.List;

public record ActivityResponseDTO(Long id,
                                  String name,
                                  float price,
                                  Duration duration,
                                  ProviderDTO providerDto,
                                  List<EmployeeDTOResponse> employeeDtoList) {
}
