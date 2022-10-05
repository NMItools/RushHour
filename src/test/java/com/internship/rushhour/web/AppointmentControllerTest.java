package com.internship.rushhour.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.appointment.service.AppointmentService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {
    private MockMvc mvc;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private JacksonTester<AppointmentDTO> jacksonTesterRequest;
    private JacksonTester<AppointmentResponseDTO> jacksonTesterResponse;

    AppointmentResponseDTO appointmentResponseDTO = TestObjectFactory.generateAppointmentResponseDTO();

    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    void canRetrieveByIdWhenExists() throws Exception {
        when(appointmentService.get(anyLong())).thenReturn(appointmentResponseDTO);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/appointment/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterResponse.write(appointmentResponseDTO).getJson());
    }

    @Test
    void resourceCreationReturnsCorrectResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        AppointmentDTO appointmentDTO = TestObjectFactory.generateAppointmentDto();

        when(appointmentService.create(any())).thenReturn(TestObjectFactory.generateAppointmentResponseDTO());

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/appointment/create").
                content(mapper.writeValueAsString(appointmentDTO))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void resourceDeletionReturnsCorrectResponse() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/appointment/25")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void updatedResponseUnmodified() throws Exception{
        AppointmentResponseDTO appointmentResponseDTO = TestObjectFactory.generateAppointmentResponseDTO();
        String jsonString ="[{\"op\":\"replace\",\"path\":\"/id\",\"value\":2}]";

        lenient().when(appointmentService.update(any(), any())).thenReturn(appointmentResponseDTO);

        MediaType mediaType = new MediaType("application", "json-patch+json");

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.patch("/appointment/update/25").content(jsonString)
                .accept(mediaType)
                .contentType(mediaType)).andReturn().getResponse();

        AppointmentResponseDTO returned = objectMapper.treeToValue(objectMapper.readTree(response.getContentAsString()),AppointmentResponseDTO.class);

        assertThat(appointmentResponseDTO.startTime()).isEqualTo(returned.startTime());
        assertThat(appointmentResponseDTO.client().accountDTO().getEmail()).isEqualTo(returned.client().accountDTO().getEmail());
    }


}
