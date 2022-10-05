package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.provider.service.ProviderService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProviderService.class, ProviderMapper.class, ActivityEmployeeMiddleMapper.class}, imports = {java.util.stream.Collectors.class})
public interface ActivityMapper {
    @Mapping(target = "providerId", expression = "java(activity.getProvider().getId())")
    @Mapping(target="employeeIds", expression = "java(activity.getEmployees().stream().map(x -> x.getId()).collect(Collectors.toList()))")
    ActivityDTO entityToDto(Activity activity);

    @Mapping(target = "provider", source = "providerId")
    @Mapping(target= "employees", source="employeeIds")
    Activity dtoToEntity(ActivityDTO activityDTO);

    @Mapping(target = "providerDto", source="provider")
    @Mapping(target="employeeDtoList", expression = "java(activityEmployeeMiddleMapper.idListToEmployeeDtoList(activity.getEmployees().stream().map(x->x.getId()).toList()))")
    ActivityResponseDTO entityToDtoResponse(Activity activity);
}
