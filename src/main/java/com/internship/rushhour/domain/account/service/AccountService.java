package com.internship.rushhour.domain.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.models.AccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountDTO getById(Long id);
    AccountDTO save(AccountDTO accountDTO);
    void delete(Long id);
    AccountDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException;
    Page<AccountDTO> getPaginated(Pageable pageable);
    Account getEntity(Long id);
    Account getByEmail(String email);
}
