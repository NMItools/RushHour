package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.employee.service.EmployeeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

// This class avoids cyclic dependency issue
@Mapper(componentModel = "spring", uses = {EmployeeService.class, EmployeeMapper.class})
public interface ActivityEmployeeMiddleMapper {
    @Mapping(source="sources", target="targets")
    Set<Employee> idListToEmployeeSet(List<Long> list);

    @Mapping(target="targets", expression = "java(employeeService.findAllById(sources))")
    List<EmployeeDTOResponse> idListToEmployeeDtoList(List<Long> list);
}
