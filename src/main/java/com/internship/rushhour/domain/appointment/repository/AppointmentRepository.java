package com.internship.rushhour.domain.appointment.repository;

import com.internship.rushhour.domain.appointment.entity.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByEmployee_AccountId(Long id, Pageable page);

    List<Appointment> findByEmployee_Provider_BusinessDomain(String domain, Pageable page);
    List<Appointment> findByClient_AccountId(Long id, Pageable page);

    @Query("SELECT CASE WHEN (count(x) > 0) then true else false end FROM Appointment x WHERE x.employee.id = ?1 AND " +
            "x.startTime >= ?2 AND x.endDate <= ?3")
    boolean isEmployeeBusy(Long employeeId, LocalDateTime start, LocalDateTime end);
}
