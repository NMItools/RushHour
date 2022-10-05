package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.models.AccountDTO;
import com.internship.rushhour.domain.role.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = RoleService.class)
public interface AccountMapper {
    @Mapping(target="role", source="roleId")
    Account accountDTOToEntity(AccountDTO source);

    @Mapping(target="roleId", expression = "java(source.getRole().getId())")
    AccountDTO accountEntityToDTO(Account source);
}
