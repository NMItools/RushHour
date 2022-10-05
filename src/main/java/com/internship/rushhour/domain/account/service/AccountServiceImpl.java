package com.internship.rushhour.domain.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.models.AccountDTO;
import com.internship.rushhour.domain.account.repository.AccountRepository;
import com.internship.rushhour.infrastructure.deserializers.CustomAccountDeserializer;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.ResourceUniqueFieldTakenException;
import com.internship.rushhour.infrastructure.mappers.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper,
                              PasswordEncoder passwordEncoder){
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AccountDTO getById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Account.class.getSimpleName()));

        return accountMapper.accountEntityToDTO(account);
    }

    @Override
    public AccountDTO save(AccountDTO accountDTO) {
        if ( accountRepository.existsByEmail(accountDTO.getEmail())){
            throw new ResourceUniqueFieldTakenException(accountDTO.getEmail(), Account.class.getSimpleName());
        }

        accountDTO.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        Account accountToBeReturned = accountRepository.save(accountMapper.accountDTOToEntity(accountDTO));

        return accountMapper.accountEntityToDTO(accountToBeReturned);
    }

    @Override
    public void delete(Long id) {
        if (!accountRepository.existsById(id)) throw new ResourceNotFoundException(id, "id", Account.class.getSimpleName());
        accountRepository.deleteById(id);
    }

    @Override
    public AccountDTO update(JsonPatch patch, Long id)
            throws JsonPatchException, JsonProcessingException {

        Account accountToBePatched = accountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Account.class.getSimpleName()));

        Account acc = applyPatchToAccount(patch, accountToBePatched);

        return accountMapper.accountEntityToDTO(accountRepository.save(acc));
    }

    private Account applyPatchToAccount(JsonPatch patch, Account targetAccount)
            throws JsonPatchException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule("CustomAccountDeserializer",
                new Version(1, 0, 0, null, null, null));

        CustomAccountDeserializer customAccountDeserializer = new CustomAccountDeserializer();
        module.addDeserializer(Account.class, customAccountDeserializer);

        objectMapper.registerModule(module);

        JsonNode patched = patch.apply(objectMapper.convertValue(targetAccount, JsonNode.class));
        return objectMapper.treeToValue(patched, Account.class);
    }

    @Override
    public Page<AccountDTO> getPaginated(Pageable pageable) {
        return accountRepository.findAll(pageable).map(accountMapper::accountEntityToDTO);
    }

    @Override
    public Account getEntity(Long id){
        return accountRepository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(id, "id", Account.class.getSimpleName()));
    }

    @Override //refactor to only return id instead of whole account
    public Account getByEmail(String email){
        return accountRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(email, "email", Account.class.getSimpleName()));
    }
}
