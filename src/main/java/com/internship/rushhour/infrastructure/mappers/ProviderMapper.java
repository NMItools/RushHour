package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    ProviderDTO entityToDto(Provider provider);
    ProviderDTOResponse entityToDtoResponse(Provider provider);
    Provider dtoToEntity(ProviderDTO providerDTO);
}

