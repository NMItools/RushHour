package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.activity.service.ActivityService;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.client.service.ClientService;
import com.internship.rushhour.domain.employee.service.EmployeeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EmployeeService.class, ClientService.class, ActivityService.class,
EmployeeMapper.class, ActivityMapper.class, ClientMapper.class})
public interface AppointmentMapper {
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "client", source = "clientId")
    @Mapping(target = "activities", source = "activities")
    Appointment dtoToEntity(AppointmentDTO appointmentDTO);


    AppointmentResponseDTO entityToResponseDto(Appointment appointment);
}
