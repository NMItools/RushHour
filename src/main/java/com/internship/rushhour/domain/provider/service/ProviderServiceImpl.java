package com.internship.rushhour.domain.provider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import com.internship.rushhour.domain.provider.repository.ProviderRepository;
import com.internship.rushhour.infrastructure.exceptions.AccessingLockedFieldException;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.ResourceUniqueFieldTakenException;
import com.internship.rushhour.infrastructure.mappers.ProviderMapper;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceImpl implements ProviderService{
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;


    @Autowired
    public ProviderServiceImpl (ProviderRepository providerRepository, ProviderMapper providerMapper){
        this.providerRepository = providerRepository;
        this.providerMapper = providerMapper;
    }

    @Override
    public ProviderDTOResponse create(ProviderDTO providerDTO) {
        if (providerRepository.existsByName(providerDTO.name())){
            throw new ResourceUniqueFieldTakenException(providerDTO.name(), Provider.class.getSimpleName());
        }
        if(providerRepository.existsByBusinessDomain(providerDTO.businessDomain())){
            throw new ResourceUniqueFieldTakenException(Provider.class.getSimpleName(), "Business Domain", providerDTO.businessDomain());
        }
        Provider mappedToEntity = providerMapper.dtoToEntity(providerDTO);
        return providerMapper.entityToDtoResponse(providerRepository.save(mappedToEntity));
    }

    @Override
    public ProviderDTOResponse get(Long id) {
        return providerMapper.entityToDtoResponse(providerRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id,"id", Provider.class.getSimpleName())));
    }

    @Override
    public Long getProviderIdFromBusinessDomain(String businessDomain){
        return providerRepository.getIdByBusinessDomain(businessDomain);
    }

    @Override
    public void delete(Long id) {
        if(!providerRepository.existsById(id)){
            throw new ResourceNotFoundException(id, "id", Provider.class.getSimpleName());
        }
        providerRepository.deleteById(id);
    }

    @Override
    public ProviderDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException {
        Provider toPatch = providerRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Provider.class.getSimpleName()));
        String oldBusinessDomain = toPatch.getBusinessDomain();

        Provider provider = applyPatchToProvider(patch, toPatch);
        
        if (!oldBusinessDomain.equals(provider.getBusinessDomain()))
            throw new AccessingLockedFieldException(AuthorizationService.getCurrentUserRole(), "businessDomain");

        return providerMapper.entityToDto(providerRepository.save(provider));
    }

    private Provider applyPatchToProvider(JsonPatch patch, Provider targetProvider)
            throws JsonPatchException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JsonNode patched = patch.apply(objectMapper.convertValue(targetProvider, JsonNode.class));
        return objectMapper.treeToValue(patched, Provider.class);
    }

    @Override
    public Page<ProviderDTOResponse> getPaginated(Pageable pageable) {
        return providerRepository.findAll(pageable).map(providerMapper::entityToDtoResponse);
    }

    @Override
    public Provider getEntity(Long id){
        return providerRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Provider.class.getSimpleName()));
    }

    @Override
    public boolean existsByName(String providerName) {
        return providerRepository.existsByName(providerName);
    }

}
