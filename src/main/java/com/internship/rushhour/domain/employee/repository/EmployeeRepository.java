package com.internship.rushhour.domain.employee.repository;

import com.internship.rushhour.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Why does this query populate a whole Account when the field is "bigInt" in the database and should be
    // accesible through x.account (as a number)
    @Query("SELECT CASE WHEN (count(x) > 0) then true else false end FROM Employee x WHERE x.account.id = ?1")
    boolean existsByAccount(Long id);

    @Query("SELECT x FROM Employee x WHERE x.provider.id = ?1")
    List<Employee> findAllByProviderId(Long id);

    boolean existsByAccountEmail(String email);

    @Query("SELECT e.account.email FROM Employee e WHERE month(e.hireDate) = ?1 and day(e.hireDate) = ?2")
    List<String> findAllByHireDate(int month, int day);

    Optional<Employee> findByAccountEmail(String email);
}
