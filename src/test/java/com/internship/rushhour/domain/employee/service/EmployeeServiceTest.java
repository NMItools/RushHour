package com.internship.rushhour.domain.employee.service;

import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.employee.repository.EmployeeRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.mappers.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    EmployeeMapper employeeMapper;

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    EmployeeServiceImpl employeeService;

    @BeforeEach
    void beforeEach(){
         lenient().when(employeeMapper.entityToDto(isA(Employee.class))).thenAnswer(i -> {
           Employee e = (Employee) i.getArguments()[0];
            return new EmployeeDTO(e.getPhone(), e.getProvider().getId(), TestObjectFactory.generateAccountDto(), e.getRatePerHour(), e.getTitle(), null);
        });

         // remember that foreign keys are currently randomised for test objects
         // when mapping from Dto to entity
        lenient().when(employeeMapper.dtoToEntity(isA(EmployeeDTO.class))).thenAnswer(i -> {
            EmployeeDTO e = (EmployeeDTO) i.getArguments()[0];
            Employee employee = new Employee();
            employee.setRatePerHour(e.ratePerHour());
            employee.setTitle(e.title());
            employee.setPhone(e.phone());
            employee.setProvider(TestObjectFactory.generateRandomisedProvider());
            employee.setAccount(TestObjectFactory.generateRandomisedAccount());
            return e;
        });

    }

    @Test
    void deleteWhenResourceNotExists(){
        when(employeeRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.delete(20L);
        });
    }

    @Test
    void getPaginatedTest(){
        List<Employee> employees = new ArrayList<>();
        employees.add(TestObjectFactory.generateRandomisedEmployee());
        employees.add(TestObjectFactory.generateRandomisedEmployee());
        employees.add(TestObjectFactory.generateRandomisedEmployee());
        employees.add(TestObjectFactory.generateRandomisedEmployee());
        employees.add(TestObjectFactory.generateRandomisedEmployee());

        when (employeeRepository.findAll(isA(Pageable.class))).thenAnswer(i ->{
            Pageable pageable = (Pageable) i.getArguments()[0];
            return new PageImpl<Employee>(employees.subList(0, pageable.getPageSize()));
        });

        int pageSize = 3;
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<EmployeeDTOResponse> employeePage = employeeService.getPaginated(pageable);
        List<EmployeeDTOResponse> employeeList = employeePage.stream().toList();

        assertThat(employeeList.size()).isEqualTo(pageSize);
        assertThat(employeePage).isNotEmpty();
    }

    @Test
    void getByIdTest(){
        Employee toBeReturned = TestObjectFactory.generateRandomisedEmployee();
        EmployeeDTOResponse employeeDTOResponse = TestObjectFactory.generateEmployeeDTOResponse(toBeReturned);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(toBeReturned));
        when(employeeMapper.entityToDtoResponse(toBeReturned)).thenReturn(employeeDTOResponse);
        EmployeeDTOResponse returnedEmployee = employeeService.get(2300L);
        assertThat(returnedEmployee.phone()+returnedEmployee.title()).isEqualTo(toBeReturned.getPhone()+toBeReturned.getTitle());
    }

}
