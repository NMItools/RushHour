package com.internship.rushhour.domain.activity.repository;

import com.internship.rushhour.domain.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository  extends JpaRepository<Activity, Long> {
    boolean existsByName(String activityName);
}
