package com.internship.rushhour.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.employee.models.EmployeeDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.employee.service.EmployeeService;
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
class EmployeeControllerTest {
    private MockMvc mvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private JacksonTester<EmployeeDTO> jacksonTester;

    private JacksonTester<EmployeeDTOResponse> jacksonTesterForResponse;

    EmployeeDTO employeeDTO = new EmployeeDTO("1234567890", 1L, TestObjectFactory.generateAccountDto(), 5.0f, "cooltitle", null);

    EmployeeDTOResponse employeeDTOResponse = new EmployeeDTOResponse(1L,"1234567890", "provideName", "name", "employee@providerName.com", 5.0f,"cooltitle", null);

    @BeforeEach
    void beforeEach() {
        ObjectMapper objectMapper = new ObjectMapper();

        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    void canRetrieveByIdWhenExists() throws Exception {
        when(employeeService.get(anyLong())).thenReturn(employeeDTOResponse);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/employee/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterForResponse.write(employeeDTOResponse).getJson());
    }

    @Test
    void resourceCreationReturnsCorrectResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/employee/create").
                content(mapper.writeValueAsString(employeeDTO))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void resourceDeletionReturnsCorrectResponse() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/employee/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}