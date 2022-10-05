package com.internship.rushhour.domain.client.models;

import com.internship.rushhour.domain.account.models.AccountDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record ClientDTO (
        @NotBlank(message="Phone can not be blank")
        @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
                message = "Invalid phone number")
        String phone,

        @NotBlank(message = "Address can not be blank")
        @Size(min=3, message = "Address must be at least 3 characters long")
        String address,

        @NotNull(message = "An account must be selected")
        @Valid
        AccountDTO accountDTO
){
}
