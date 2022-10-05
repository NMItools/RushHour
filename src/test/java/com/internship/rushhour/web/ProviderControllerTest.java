package com.internship.rushhour.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import com.internship.rushhour.domain.provider.service.ProviderService;
import com.internship.rushhour.infrastructure.exceptions.ControllerAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProviderControllerTest {
    private MockMvc mvc;

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private ProviderController providerController;

    private JacksonTester<ProviderDTO> jacksonTester;
    private JacksonTester<ProviderDTOResponse> jacksonTesterForResponse;

    ProviderDTO testDTO = new ProviderDTO("testname", "website.com", "com",
            "0889800808", LocalTime.parse("09:00"), LocalTime.parse("17:00"), new HashSet<String>(Arrays.asList("MON", "TUE")));

    ProviderDTOResponse providerDTOResponse = new ProviderDTOResponse(1L,"testname", "website.com", "com",
            "0889800808", LocalTime.parse("09:00"), LocalTime.parse("17:00"), new HashSet<>(Arrays.asList("MON", "TUE")));

    @BeforeEach
    void beforeEach() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(providerController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    void canRetrieveByIdWhenExists() throws Exception {
        when(providerService.get(anyLong())).thenReturn(providerDTOResponse);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/provider/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterForResponse.write(providerDTOResponse).getJson());
    }

    @Test
    void resourceDeletionReturnsCorrectResponse() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/provider/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
