package com.internship.rushhour.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.activity.service.ActivityService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/activity")
@Tag(name = "Activity", description="The Activity Api")
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService){
        this.activityService = activityService;
    }

    @Operation(summary = "Create an activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid activity details",
                    content = @Content) })
    @PostMapping("/create")
    @PreAuthorize("@autho.isAdministratorOfProvider(#activityDTO.providerId(), principal)")
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityDTO activityDTO){
        return new ResponseEntity<>(activityService.create(activityDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get an activity by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity located by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivity(@PathVariable Long id){
        return ResponseEntity.ok(activityService.get(id));
    }

    @Operation(summary = "Delete an activity by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Activity id not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@autho.canCRUDActivity(#id, principal)")
    public void deleteActivity(@PathVariable Long id){
        activityService.delete(id);
    }

    @Operation(summary = "Update an activity using json patch and its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid update information",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Activity id not found",
                    content = @Content) })
    @PatchMapping(path = "/update/{id}", consumes = "application/json-patch+json")
    @PreAuthorize("@autho.canCRUDActivity(#id, principal)")
    public ResponseEntity<ActivityResponseDTO> updateActivity(@PathVariable Long id, @RequestBody JsonPatch patch)
            throws JsonPatchException, JsonProcessingException {

        ActivityResponseDTO activityResponseDTO = activityService.update(patch, id);
        return ResponseEntity.ok(activityResponseDTO);
    }

    @Operation(summary = "Get a page of activities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page successfully retrieved",
                    content = @Content(array= @ArraySchema(schema = @Schema(implementation = ActivityResponseDTO.class))))})
    @GetMapping
    public ResponseEntity<Page<ActivityResponseDTO>> getPaginated(Pageable page){
        return ResponseEntity.ok(activityService.getPaginated(page));
    }
}
