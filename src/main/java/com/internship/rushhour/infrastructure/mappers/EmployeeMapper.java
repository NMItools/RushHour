package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.account.service.AccountService;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.provider.service.ProviderService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProviderService.class, AccountService.class, AccountMapper.class})
public interface EmployeeMapper {
    @Mapping(target="providerId", expression = "java(source.getProvider().getId())")
    @Mapping(target="accountDTO", source="account")
    EmployeeDTO entityToDto(Employee source);

    @Mapping(target="provider", source = "providerId")
    Employee dtoToEntity(EmployeeDTO source);

    @Mapping(target="name", expression = "java(employee.getAccount().getName())")
    @Mapping(target="providerName", expression = "java(employee.getProvider().getName())")
    @Mapping(target="email", expression = "java(employee.getAccount().getEmail())")
    @Mapping(target="hireDate", source = "hireDate")
    EmployeeDTOResponse entityToDtoResponse(Employee employee);

}
