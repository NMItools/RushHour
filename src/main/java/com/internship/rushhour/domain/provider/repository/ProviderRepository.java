package com.internship.rushhour.domain.provider.repository;

import com.internship.rushhour.domain.provider.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    boolean existsByName(String name);
    Provider findByBusinessDomain(String businessDomain);

    @Query("SELECT x.id FROM Provider x WHERE x.businessDomain=?1")
    Long getIdByBusinessDomain(String businessDomain);

    boolean existsByBusinessDomain(String businessDomain);
}
