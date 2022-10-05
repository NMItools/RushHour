package com.internship.rushhour.domain.role.repository;

import com.internship.rushhour.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Override
    Optional<Role> findById(Long aLong);

    boolean existsByName(String name);
}
