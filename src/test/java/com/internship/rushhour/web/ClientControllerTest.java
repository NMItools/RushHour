package com.internship.rushhour.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import com.internship.rushhour.domain.client.service.ClientService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {
    private MockMvc mvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private JacksonTester<ClientDTO> jacksonTester;

    private JacksonTester<ClientDTOResponse> jacksonTesterForResponse;

    ClientDTO clientDTO = new ClientDTO("1234567890", "addressABC", TestObjectFactory.generateAccountDto());

    ClientDTOResponse clientDTOResponse = new ClientDTOResponse(1L, "1234567890", "addressABC", TestObjectFactory.generateAccountDto());

    @BeforeEach
    void beforeEach() {
        ObjectMapper objectMapper = new ObjectMapper();

        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    void canRetrieveByIdWhenExists() throws Exception {
        when(clientService.get(anyLong())).thenReturn(clientDTOResponse);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/client/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterForResponse.write(clientDTOResponse).getJson());
    }

    @Test
    void resourceCreationReturnsCorrectResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/client/create").
                content(mapper.writeValueAsString(clientDTO))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void resourceDeletionReturnsCorrectResponse() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/client/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
