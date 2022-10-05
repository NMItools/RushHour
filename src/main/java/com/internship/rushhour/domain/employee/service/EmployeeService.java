package com.internship.rushhour.domain.employee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeAdminDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeDTOResponse create(EmployeeDTO employeeDTO);
    EmployeeDTOResponse get(Long id);
    void delete(Long id);
    EmployeeDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException;
    Page<EmployeeDTOResponse> getPaginated(Pageable pageable);
    Employee getEntity(Long id);
    List<EmployeeDTOResponse> findAllById(List<Long> id);
    Page<EmployeeDTOResponse> getProviderEmployees(Pageable pageable);
    EmployeeDTOResponse createAdminWithProvider(EmployeeAdminDTO employeeAdminDTO);
    Optional<Employee> findByAccountEmail(String name);
    List<String> findAllByHireDate(int month, int day);
    Employee findByEmail(String email);
}
