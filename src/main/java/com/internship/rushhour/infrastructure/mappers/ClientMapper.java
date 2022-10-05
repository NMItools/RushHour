package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.account.service.AccountService;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccountService.class, AccountMapper.class})
public interface ClientMapper {
    Client dtoToEntity(ClientDTO clientDTO);

    @Mapping(target = "accountDTO", source = "account")
    ClientDTO entityToDto(Client client);

    @Mapping(target = "accountDTO", source = "account")
    ClientDTOResponse entityToDtoResponse(Client client);
}
