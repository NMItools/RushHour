package com.internship.rushhour.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import com.internship.rushhour.domain.provider.service.ProviderService;
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

@RestController
@RequestMapping("/provider")
@Tag(name = "Provider", description="The Provider Api")
public class ProviderController {
    private final ProviderService providerService;

    @Autowired
    public ProviderController(ProviderService providerService){
        this.providerService = providerService;
    }

    @Operation(summary = "Get a provider by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider located by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProviderDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Provider not found",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<ProviderDTOResponse> getProvider(@PathVariable Long id){
        return ResponseEntity.ok(providerService.get(id));
    }

    @Operation(summary = "Delete a provider by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Provider id not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@autho.isAdministratorOfProvider(#id, principal)")
    public void deleteProvider(@PathVariable Long id){
        providerService.delete(id);
    }

    @Operation(summary = "Update a provider using json patch and its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProviderDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid update information",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Provider id not found",
                    content = @Content) })
    @PatchMapping(path = "/update/{id}", consumes = "application/json-patch+json")
    @PreAuthorize("@autho.isAdministratorOfProvider(#id, principal)")
    public ResponseEntity<ProviderDTO> updateProvider(@PathVariable Long id, @RequestBody JsonPatch patch)
            throws JsonPatchException, JsonProcessingException {

        ProviderDTO providerDTO = providerService.update(patch, id);
        return ResponseEntity.ok(providerDTO);
    }


    @Operation(summary = "Get a page of providers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page successfully retrieved",
                    content = @Content(array= @ArraySchema(schema = @Schema(implementation = ProviderDTO.class))))})
    @GetMapping()
    public ResponseEntity<Page<ProviderDTOResponse>> getPaginated(Pageable page){
        return ResponseEntity.ok(providerService.getPaginated(page));
    }

}
