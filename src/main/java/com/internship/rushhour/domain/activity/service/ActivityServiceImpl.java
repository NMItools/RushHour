package com.internship.rushhour.domain.activity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.activity.repository.ActivityRepository;
import com.internship.rushhour.infrastructure.deserializers.CustomActivityDeserializer;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.mappers.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, ActivityMapper activityMapper){
        this.activityMapper = activityMapper;
        this.activityRepository = activityRepository;
    }

    @Override
    public ActivityResponseDTO create(ActivityDTO activityDto) {
        Activity toBeCreated = activityMapper.dtoToEntity(activityDto);
        if (!activityEmployeesBelongToSameProvider(toBeCreated))
            throw new UserActionNeededException("Please remove employees which do not belong to your Provider from your request");

        return activityMapper.entityToDtoResponse(activityRepository
                .save(activityMapper.dtoToEntity(activityDto)));
    }

    @Override
    public ActivityResponseDTO get(Long id) {
        return activityMapper.entityToDtoResponse(activityRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Activity.class.getSimpleName())));
    }

    @Override
    public void delete(Long id) {
        if (!activityRepository.existsById(id))
            throw new ResourceNotFoundException(id, "id", Activity.class.getSimpleName());
        activityRepository.deleteById(id);
    }

    @Override
    public ActivityResponseDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException {
        Activity toPatch = activityRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Activity.class.getSimpleName()));

        Activity activity = applyPatchToActivity(patch, toPatch);
        return activityMapper.entityToDtoResponse(activityRepository.save(activity));
    }

    private Activity applyPatchToActivity(JsonPatch patch, Activity targetActivity)
            throws JsonPatchException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule simpleModule = new SimpleModule();
        CustomActivityDeserializer customActivityDeserializer = new CustomActivityDeserializer();
        simpleModule.addDeserializer(Activity.class, customActivityDeserializer);
        objectMapper.registerModule(simpleModule);

        Activity emptyActivity = new Activity();
        emptyActivity.setId(targetActivity.getId());
        emptyActivity.setEmployees(targetActivity.getEmployees());

        JsonNode patched = patch.apply(objectMapper.convertValue(emptyActivity, JsonNode.class));
        return objectMapper.treeToValue(patched, Activity.class);
    }

    @Override
    public Page<ActivityResponseDTO> getPaginated(Pageable pageable) {
        return activityRepository.findAll(pageable).map(activityMapper::entityToDtoResponse);
    }

    @Override
    public Activity getEntity(Long id){
        return activityRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Activity.class.getSimpleName()));
    }

    private boolean activityEmployeesBelongToSameProvider(Activity activity){
        return activity.getEmployees().stream().map(x -> x.getProvider().getId()).filter(x -> !x.equals(activity.getProvider()
                .getId())).toList().isEmpty();
    }

    @Override
    public List<Activity> findAllById(List<Long> id){
        return activityRepository.findAllById(id);
    }

    @Override
    public boolean existsByName(String activityName) {
        return activityRepository.existsByName(activityName);
    }

}
