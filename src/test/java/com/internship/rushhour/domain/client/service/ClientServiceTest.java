package com.internship.rushhour.domain.client.service;

import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import com.internship.rushhour.domain.client.repository.ClientRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.mappers.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    ClientRepository clientRepository;

    @Mock
    ClientMapper clientMapper;

    @InjectMocks
    ClientServiceImpl clientService;

    @BeforeEach
    void beforeEach(){
        lenient().when(clientMapper.entityToDto(isA(Client.class))).thenAnswer(i -> {
            Client c = (Client) i.getArguments()[0];
            return new ClientDTO(c.getPhone(), c.getAddress(), TestObjectFactory.generateAccountDto());
        });

        lenient().when(clientMapper.dtoToEntity(isA(ClientDTO.class))).thenAnswer(i -> {
           ClientDTO c = (ClientDTO) i.getArguments()[0];
           Client client = new Client();
           client.setPhone(c.phone());
           client.setAddress(c.address());
           client.setAccount(TestObjectFactory.generateRandomisedAccount());
           return client;
        });
    }

    @Test
    void deleteWhenResourceNotExists(){
        when(clientRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.delete(20L);
        });
    }

    @Test
    void getPaginatedTest(){
        List<Client> clients = new ArrayList<>();
        clients.add(TestObjectFactory.generateRandomisedClient());
        clients.add(TestObjectFactory.generateRandomisedClient());
        clients.add(TestObjectFactory.generateRandomisedClient());
        clients.add(TestObjectFactory.generateRandomisedClient());
        clients.add(TestObjectFactory.generateRandomisedClient());

        when (clientRepository.findAll(isA(Pageable.class))).thenAnswer(i ->{
            Pageable pageable = (Pageable) i.getArguments()[0];
            return new PageImpl<Client>(clients.subList(0, pageable.getPageSize()));
        });

        int pageSize = 3;
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<ClientDTOResponse> clientPage = clientService.getPaginated(pageable);
        List<ClientDTOResponse> clientList = clientPage.stream().toList();

        assertThat(clientList.size()).isEqualTo(pageSize);
        assertThat(clientPage).isNotEmpty();
    }

    @Test
    void getByIdTest(){
        Client toBeReturned = TestObjectFactory.generateRandomisedClient();
        ClientDTOResponse clientDTOResponse = TestObjectFactory.generateClientDtoResponse(toBeReturned);
        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(toBeReturned));
        when(clientMapper.entityToDtoResponse(toBeReturned)).thenReturn(clientDTOResponse);
        ClientDTOResponse returnedClient = clientService.get(2300L);
        assertThat(returnedClient.phone()+returnedClient.address()).isEqualTo(toBeReturned.getPhone()+toBeReturned.getAddress());
    }
}