package com.internship.rushhour.domain.activity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {
    ActivityResponseDTO create(ActivityDTO activityDTO);
    ActivityResponseDTO get(Long id);
    void delete(Long id);
    ActivityResponseDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException;
    Page<ActivityResponseDTO> getPaginated(Pageable pageable);
    Activity getEntity(Long id);
    List<Activity> findAllById(List<Long> id);
    boolean existsByName(String activityName);
}
