package com.internship.rushhour.domain.provider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProviderService {
    ProviderDTOResponse create(ProviderDTO providerDTO);
    ProviderDTOResponse get(Long id);
    void delete(Long id);
    ProviderDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException;
    Page<ProviderDTOResponse> getPaginated(Pageable pageable);
    Provider getEntity(Long id);
    Long getProviderIdFromBusinessDomain(String businessDomain);
    boolean existsByName(String providerName);
}
