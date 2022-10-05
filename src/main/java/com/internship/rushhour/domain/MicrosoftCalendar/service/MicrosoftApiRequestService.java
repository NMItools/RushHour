package com.internship.rushhour.domain.MicrosoftCalendar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.rushhour.domain.MicrosoftCalendar.entity.MicrosoftCalendar;
import com.internship.rushhour.domain.MicrosoftCalendar.models.MicrosoftResponse;
import com.internship.rushhour.domain.MicrosoftCalendar.models.RefreshTokenPayload;
import com.internship.rushhour.domain.MicrosoftCalendar.repository.MicrosoftCalendarRepository;
import com.internship.rushhour.infrastructure.encryptors.EncryptionUtil;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

@Service
public class MicrosoftApiRequestService {
    private final Logger logger = LoggerFactory.getLogger(MicrosoftApiRequestService.class);

    private final MicrosoftCalendarRepository microsoftCalendarRepository;
    private final EncryptionUtil encryptionUtil;
    private final String clientId;
    private final String clientSecret;
    private final String scopes;
    private final RestTemplate restTemplate;
    private final String tokenPath;
    private final String verificationPath;

    @Autowired
    public MicrosoftApiRequestService(MicrosoftCalendarRepository microsoftCalendarRepository, EncryptionUtil encryptionUtil,
                                      RestTemplate restTemplate,
                                      @Value("${microsoft.calendar.service.client.id}") String clientId,
                                      @Value("${microsoft.calendar.service.client.secret}") String clientSecret,
                                      @Value("${microsoft.calendar.service.scopes}") String scopes,
                                      @Value("${microsoft.calendar.service.token.path}") String tokenPath,
                                      @Value("${microsoft.calendar.service.verification.uri}") String verificationPath){
        this.microsoftCalendarRepository = microsoftCalendarRepository;
        this.encryptionUtil = encryptionUtil;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scopes = scopes;
        this.restTemplate = restTemplate;
        this.tokenPath = tokenPath;
        this.verificationPath = verificationPath;
    }

    public String genericMicrosoftHttpCaller(HttpMethod method, String body, String url) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        MicrosoftCalendar calendar = refreshTokenIfExpired();
        String returnValue = "Default return value - something went wrong";
        String expectedResponse = getExpectedResponseCode(method);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + encryptionUtil.doAESDecryption(calendar.getAccessToken()));

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, method, request, String.class );

        String responseCode = response.toString().substring(1,4);

        if (!responseCode.equals(expectedResponse)){
            logger.error("Expected response was " + expectedResponse + "but received " + responseCode);
            throw new UserActionNeededException("Expected response was " +
                    expectedResponse + "but received " +
                    responseCode + "\n" +response.getBody());
        } else {
            returnValue = responseCode;
            if (responseCode.equals("201") ) returnValue = new ObjectMapper().readTree(response.getBody().toString())
                    .get("id").asText();
        }

        return returnValue;
    }

    private String getExpectedResponseCode(HttpMethod httpMethod){
        if ( httpMethod == HttpMethod.DELETE ) return "204";
        else if (httpMethod == HttpMethod.POST ) return "201";
        else if (httpMethod == HttpMethod.PATCH ) return "200";
        return "sanity check";
    }

    private MicrosoftResponse refreshToken(MicrosoftCalendar calendar) throws JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String url = tokenPath;

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("client_id", this.clientId);
        map.add("scope", this.scopes);
        // the below redirect uri will work because the original access code was generated with it, the field is apparently
        // only used for verification purposes
        map.add("redirect_uri", verificationPath);
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", encryptionUtil.doAESDecryption(calendar.getRefreshToken()));
        map.add("client_secret", this.clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody().toString());

        MicrosoftResponse microsoftResponse = new MicrosoftResponse();
        microsoftResponse.setAccessToken(jsonNode.get("access_token").asText());
        microsoftResponse.setRefreshToken(jsonNode.get("refresh_token").asText());
        microsoftResponse.setExpiresInSeconds(jsonNode.get("expires_in").asText());

        return microsoftResponse;
    }

    private MicrosoftCalendar refreshTokenIfExpired() throws JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        MicrosoftCalendar calendar = microsoftCalendarRepository.findById(1l)
                .orElseThrow(() -> new ResourceNotFoundException(1l, "id", MicrosoftCalendar.class.getSimpleName()));

        if (calendar.getExpiresAt().before(Date.from(Instant.now().minusSeconds(1L))) || calendar.getAccessToken() == null){
            MicrosoftResponse microsoftResponse = refreshToken(calendar);
            calendar.setExpiresAt(Date.from(Instant.now().plusSeconds(Long.valueOf(microsoftResponse.getExpiresInSeconds()))));
            calendar.setRefreshToken(encryptionUtil.doAESEncryption(microsoftResponse.getRefreshToken()));
            calendar.setAccessToken(encryptionUtil.doAESEncryption(microsoftResponse.getAccessToken()));
            microsoftCalendarRepository.save(calendar);
            logger.info("Token expired, refreshing - new exp date: " + calendar.getExpiresAt().toString());
        }
        return calendar;
    }

    public void acceptFirstRefreshToken(RefreshTokenPayload refreshTokenPayload) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        MicrosoftCalendar calendar;
        if (microsoftCalendarRepository.existsById(1L)){
            calendar = microsoftCalendarRepository.getById(1l);
            calendar.setRefreshToken(encryptionUtil.doAESEncryption(refreshTokenPayload.getRefresh_token()));
        } else {
            calendar = new MicrosoftCalendar();
            calendar.setExpiresAt(Date.from(Instant.now()));
            calendar.setRefreshToken(encryptionUtil.doAESEncryption(refreshTokenPayload.getRefresh_token()));
        }
        microsoftCalendarRepository.save(calendar);
        refreshTokenIfExpired();
    }
}
