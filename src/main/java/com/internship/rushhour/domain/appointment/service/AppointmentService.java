package com.internship.rushhour.domain.appointment.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.infrastructure.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

public interface AppointmentService {
    AppointmentResponseDTO create(AppointmentDTO appointmentDTO) throws GeneralSecurityException, IOException, ParseException;
    AppointmentResponseDTO get(Long id);
    void delete(Long id) throws GeneralSecurityException, IOException;
    AppointmentResponseDTO update(JsonPatch patch, Long id) throws JsonPatchException, IOException, GeneralSecurityException, ParseException;
    Page<AppointmentResponseDTO> getPaginated(Pageable pageable, CustomUserDetails user);
}
