package com.internship.rushhour.domain.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.service.AccountService;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import com.internship.rushhour.domain.client.repository.ClientRepository;
import com.internship.rushhour.infrastructure.deserializers.CustomClientDeserializer;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.ResourceUniqueFieldTakenException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.mail.EmailService;
import com.internship.rushhour.infrastructure.mappers.AccountMapper;
import com.internship.rushhour.infrastructure.mappers.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AccountMapper accountMapper;
    private final AccountService accountService;
    private final EmailService emailService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, ClientMapper clientMapper, AccountMapper accountMapper,
                             AccountService accountService, EmailService emailService){
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.accountMapper = accountMapper;
        this.accountService = accountService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ClientDTOResponse create(ClientDTO clientDTO) {
        if (clientRepository.existsByAccountEmail(clientDTO.accountDTO().getEmail())){
            throw new ResourceUniqueFieldTakenException(clientDTO.accountDTO().getEmail(), Client.class.getSimpleName());
        }
        Client client = clientMapper.dtoToEntity(clientDTO);
        Account account = accountMapper.accountDTOToEntity(accountService.save(clientDTO.accountDTO()));
        account.setId(accountService.getByEmail(account.getEmail()).getId());
        client.setAccount(account);

        if (!account.getRole().getName().equals("CLIENT")) throw new UserActionNeededException("Incorrect role selected");

        client = clientRepository.save(client);
        emailService.sendAccountCreated(client.getAccount().getEmail(), client.getAccount().getName());

        return clientMapper.entityToDtoResponse(clientRepository.save(client));
    }

    @Override
    public ClientDTOResponse get(Long id) {
        return clientMapper.entityToDtoResponse(clientRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException(id, "id", Client.class.getSimpleName())
        ));
    }

    @Override
    public void delete(Long id) {
        if (!clientRepository.existsById(id)){
            throw new ResourceNotFoundException(id, "id", Client.class.getSimpleName());
        }
        clientRepository.deleteById(id);
    }

    @Override
    public ClientDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException {
        Client toPatch = clientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Client.class.getSimpleName()));
        Client patched = applyPatchToClient(patch, toPatch);
        return clientMapper.entityToDto(clientRepository.save(patched));
    }

    private Client applyPatchToClient(JsonPatch patch, Client target) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        CustomClientDeserializer customClientDeserializer = new CustomClientDeserializer();
        simpleModule.addDeserializer(Client.class, customClientDeserializer);
        objectMapper.registerModule(simpleModule);

        Client client = new Client();
        client.setId(target.getId());
        JsonNode patched = patch.apply(objectMapper.convertValue(client, JsonNode.class));

        return objectMapper.treeToValue(patched, Client.class);
    }

    @Override
    public Page<ClientDTOResponse> getPaginated(Pageable pageable) {
        return clientRepository.findAll(pageable).map(clientMapper::entityToDtoResponse);
    }

    @Override
    public Client getEntity(Long id){
        return clientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Client.class.getSimpleName()));
    }

    @Override
    public Client getByAccountEmail(String clientEmail) {
        return clientRepository.findByAccountEmail(clientEmail)
                .orElseThrow(() -> new ResourceNotFoundException
                        (clientEmail, "client email", Client.class.getSimpleName()));
    }

}
