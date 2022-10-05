package com.internship.rushhour.domain.client.repository;

import com.internship.rushhour.domain.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT CASE WHEN (count(x) > 0) then true else false end FROM Client x WHERE x.account.id = ?1")
    boolean existsByAccount(Long id);
    boolean existsByAccountEmail(String email);
    Optional<Client> findByAccountEmail(String email);
}
