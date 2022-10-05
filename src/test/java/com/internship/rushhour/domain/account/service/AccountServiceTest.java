package com.internship.rushhour.domain.account.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.models.AccountDTO;
import com.internship.rushhour.domain.account.repository.AccountRepository;
import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.mappers.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void beforeEach(){
        lenient().when(accountMapper.accountDTOToEntity(isA(AccountDTO.class))).thenAnswer(i ->{
            AccountDTO accountDTO1 = (AccountDTO) i.getArguments()[0];
            return new Account(1L, accountDTO1.getEmail(), accountDTO1.getName(), accountDTO1.getPassword(), new Role());
        });

        lenient().when(accountMapper.accountEntityToDTO(isA(Account.class))).thenAnswer(i ->{
            Account a = (Account) i.getArguments()[0];
            return new AccountDTO(a.getEmail(), a.getName(), a.getPassword(), 1L);
        });
    }

    @Test
    void jsonPatchExceptionTest() throws IOException {
        AccountDTO accountDTO = new AccountDTO("admin8@admin.com", "Administrator", "plainTextPassword",
                1L);
        Account account = new Account(1L, accountDTO.getEmail(), accountDTO.getName(), accountDTO.getPassword(), new Role());

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        // exception triggered by a path being set to "fake" instead of a valid property of Account
        String jsonString = "[{\"op\": \"replace\",\"path\": \"/fake\",\"value\": \"testname\"}]";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        JsonPatch patch = JsonPatch.fromJson(jsonNode);

        assertThatExceptionOfType(JsonPatchException.class ).isThrownBy(() ->
                accountService.update(patch, 1L));
    }

    @Test
    void deleteResourceNotFoundExceptionTest(){
        when(accountRepository.existsById(anyLong())).thenReturn(false);
        Long idOfAccountToDelete = 50L;

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.delete(idOfAccountToDelete);
        });
    }

    @Test
    void getPaginatedTest(){
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account(1L, "email@email.com","testname","password", new Role()));
        accountList.add(new Account(2L, "email2@email.com","testname","password", new Role()));
        accountList.add(new Account(3L, "email3@email.com","testname","password", new Role()));
        accountList.add(new Account(4L, "email4@email.com","testname","password", new Role()));
        accountList.add(new Account(5L, "email5@email.com","testname","password", new Role()));

        when(accountRepository.findAll(isA(Pageable.class))).thenAnswer(i -> {
            Pageable p = (Pageable) i.getArguments()[0];
            return new PageImpl<Account>(accountList.subList(0, p.getPageSize()));
        });

        int pageableSize = 2;
        Pageable p = Pageable.ofSize(pageableSize);

        Page<AccountDTO> resultPage = accountService.getPaginated(Pageable.ofSize(2));
        List<AccountDTO> resultList = resultPage.stream().toList();

        assertThat(resultList.size()).isEqualTo(pageableSize);
        assertThat(resultList.get(pageableSize-1).getEmail()).isEqualTo(accountList.get(pageableSize-1).getEmail());
    }


    @Test
    void getByIdTest(){
        Account toBeRetrieved = new Account(1L, "testmail@mail.com", "thename", "password", new Role());
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(toBeRetrieved));

        AccountDTO returnedAccount = accountService.getById(5L);

        assertThat(returnedAccount.getEmail()).isEqualTo(toBeRetrieved.getEmail());
    }

}