package com.internship.rushhour.web;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.appointment.service.AppointmentService;
import com.internship.rushhour.infrastructure.security.CustomUserDetails;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

@RestController
@RequestMapping("/appointment")
@Tag(name = "Appointment", description="The Appointment Api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Create an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid appointment details",
                    content = @Content) })
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentDTO dto) throws GeneralSecurityException,
            IOException, ParseException {
        return new ResponseEntity<>(appointmentService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get an appointment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment located by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content) })
    @GetMapping("/{id}")
    @PreAuthorize("@autho.isPartOfAppointment(#id, principal)")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.get(id));
    }

    @Operation(summary = "Delete an appointment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment id not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@autho.isPartOfAppointment(#id, principal)")
    public void deleteAppointment(@PathVariable Long id) throws GeneralSecurityException, IOException {
        appointmentService.delete(id);
    }

    @Operation(summary = "Update an appointment using json patch and its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid update information",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment id not found",
                    content = @Content) })
    @PatchMapping(path = "/update/{id}", consumes = "application/json-patch+json")
    @PreAuthorize("@autho.isPartOfAppointment(#id, principal)")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody JsonPatch patch)
            throws JsonPatchException, GeneralSecurityException, IOException, ParseException {

        AppointmentResponseDTO dto = appointmentService.update(patch, id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get a page of appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page successfully retrieved",
                    content = @Content(array= @ArraySchema(schema = @Schema(implementation = AppointmentResponseDTO.class))))})
    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> getPaginated(Pageable page){
        return ResponseEntity.ok(appointmentService.getPaginated(page, (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
    }
}
