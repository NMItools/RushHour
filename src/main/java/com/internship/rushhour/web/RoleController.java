package com.internship.rushhour.web;

import com.internship.rushhour.domain.role.models.RoleCreateDTO;
import com.internship.rushhour.domain.role.models.RoleGetDTO;
import com.internship.rushhour.domain.role.service.RoleService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@Tag(name="Role", description = "The Role API")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping("/new")
    @Operation(summary = "Create a new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Found the account",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleCreateDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid role information",
                    content = @Content) })
    public ResponseEntity<RoleCreateDTO> createRole(@RequestBody RoleCreateDTO role){
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get a page of roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account successfully updated",
                    content = @Content(array= @ArraySchema(schema = @Schema(implementation = RoleCreateDTO.class))))})
    public Page<RoleGetDTO> getPaginated(Pageable page) { return roleService.getPaginated(page); }

    @Operation(summary = "Delete a role by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Role id not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long id){
        roleService.deleteRole(id);
    }
}
