package com.internship.rushhour.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.activity.service.ActivityService;
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
class ActivityControllerTest {
    private MockMvc mvc;

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    private JacksonTester<ActivityDTO> jacksonTesterRequest;
    private JacksonTester<ActivityResponseDTO> jacksonTesterResponse;

    ActivityResponseDTO activityResponseDTO = TestObjectFactory.generateActivityResponseDto();

    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(activityController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    void canRetrieveByIdWhenExists() throws Exception {
        when(activityService.get(anyLong())).thenReturn(activityResponseDTO);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/activity/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterResponse.write(activityResponseDTO).getJson());
    }

    @Test
    void resourceCreationReturnsCorrectResponse() throws Exception {

        ActivityDTO activityDTO = TestObjectFactory.generateActivityDto();

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/activity/create").
                content(objectMapper.writeValueAsString(activityDTO))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void resourceDeletionReturnsCorrectResponse() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/activity/25")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}