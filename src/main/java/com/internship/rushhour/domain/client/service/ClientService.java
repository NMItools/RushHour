package com.internship.rushhour.domain.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientDTOResponse create(ClientDTO clientDTO);
    ClientDTOResponse get(Long id);
    void delete(Long id);
    ClientDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException;
    Page<ClientDTOResponse> getPaginated(Pageable pageable);
    Client getEntity(Long id);
    Client getByAccountEmail(String clientEmail);
}
