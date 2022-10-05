package com.internship.rushhour.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import com.internship.rushhour.domain.client.service.ClientService;
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
@RequestMapping("/client")
@Tag(name = "Client", description="The Client Api")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @Operation(summary = "Create a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid client details",
                    content = @Content) })
    @PostMapping("/create")
    public ResponseEntity<ClientDTOResponse> createClient(@Valid @RequestBody ClientDTO clientDTO){
        return new ResponseEntity<>(clientService.create(clientDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a client by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client located by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content) })
    @GetMapping("/{id}")
    @PreAuthorize("@autho.isOwnerOfClient(#id, principal)")
    public ResponseEntity<ClientDTOResponse> getClient(@PathVariable Long id){
        return ResponseEntity.ok(clientService.get(id));
    }

    @Operation(summary = "Delete a client by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Client id not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@autho.isOwnerOfClient(#id, principal)")
    public void deleteClient(@PathVariable Long id){
        clientService.delete(id);
    }

    @Operation(summary = "Update a client using json patch and its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid update information",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Client id not found",
                    content = @Content) })
    @PatchMapping(path = "/update/{id}", consumes = "application/json-patch+json")
    @PreAuthorize("@autho.isOwnerOfClient(#id, principal)")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody JsonPatch patch)
            throws JsonPatchException, JsonProcessingException {

        ClientDTO clientDTO = clientService.update(patch, id);
        return ResponseEntity.ok(clientDTO);
    }

    @Operation(summary = "Get a page of clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page successfully retrieved",
                    content = @Content(array= @ArraySchema(schema = @Schema(implementation = ClientDTO.class))))})
    @GetMapping
    public ResponseEntity<Page<ClientDTOResponse>> getPaginated(Pageable page){
        return ResponseEntity.ok(clientService.getPaginated(page));
    }
}
