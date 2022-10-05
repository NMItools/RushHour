package com.internship.rushhour.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.employee.models.EmployeeAdminDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/employee")
@Tag(name = "Employee", description="The Employee Api")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid employee details",
                    content = @Content) })
    @PostMapping("/create")
    public ResponseEntity<EmployeeDTOResponse> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO){
        return new ResponseEntity<>(employeeService.create(employeeDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Create an administrator provider together with his provider (first use)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Provider and administrator successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid employee or provider details",
                    content = @Content) })
    @PostMapping("/createProviderAdmin")
    public ResponseEntity<EmployeeDTOResponse> createProviderAdministrator(@Valid @RequestBody EmployeeAdminDTO employeeAdminDTO){
        return new ResponseEntity<>(employeeService.createAdminWithProvider(employeeAdminDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get an employee by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee located by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content) })
    @GetMapping("/{id}")
    @PreAuthorize("@autho.hasAccessToEmployee(#id, principal)")
    public ResponseEntity<EmployeeDTOResponse> getEmployee(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.get(id));
    }

    @Operation(summary = "Delete an employee by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee id not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@autho.isOwnerOfEmployee(#id, principal) || @autho.isOwnerOfEmployeeProvider(#id, principal)")
    public void deleteEmployee(@PathVariable Long id){
        employeeService.delete(id);
    }

    @Operation(summary = "Update an employee using json patch and its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid update information",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee id not found",
                    content = @Content) })
    @PatchMapping(path = "/update/{id}", consumes = "application/json-patch+json")
    @PreAuthorize("@autho.isOwnerOfEmployee(#id, principal) || @autho.isOwnerOfEmployeeProvider(#id, principal)")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody JsonPatch patch)
            throws JsonPatchException, JsonProcessingException {

        EmployeeDTO employeeDTO = employeeService.update(patch, id);
        return ResponseEntity.ok(employeeDTO);
    }

    @Operation(summary = "Get a page of employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page successfully retrieved",
                    content = @Content(array= @ArraySchema(schema = @Schema(implementation = EmployeeDTO.class))))})
    @GetMapping
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<Page<EmployeeDTOResponse>> getPaginated(Pageable page){
        return ResponseEntity.ok(employeeService.getProviderEmployees(page));
    }
}
