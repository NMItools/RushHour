package com.internship.rushhour.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.internship.rushhour.domain.MicrosoftCalendar.models.RefreshTokenPayload;
import com.internship.rushhour.domain.MicrosoftCalendar.service.MicrosoftApiRequestService;
import com.internship.rushhour.domain.MicrosoftCalendar.service.MicrosoftCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/mcalendar")
public class MicrosoftCalendarController {
    private final MicrosoftApiRequestService microsoftApiRequestService;

    @Autowired
    public MicrosoftCalendarController(MicrosoftApiRequestService microsoftApiRequestService){
        this.microsoftApiRequestService = microsoftApiRequestService;
    }

    @PostMapping("/refreshToken")
    public ResponseEntity addRefreshToken(@RequestBody RefreshTokenPayload refreshTokenPayload) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        microsoftApiRequestService.acceptFirstRefreshToken(refreshTokenPayload);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
