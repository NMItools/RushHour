package com.internship.rushhour.domain.client.models;

import com.internship.rushhour.domain.account.models.AccountDTO;

public record ClientDTOResponse(
        Long id,
        String phone,
        String address,
        AccountDTO accountDTO
){
}
