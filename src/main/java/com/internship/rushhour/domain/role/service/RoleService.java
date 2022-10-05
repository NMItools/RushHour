package com.internship.rushhour.domain.role.service;

import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.domain.role.models.RoleCreateDTO;
import com.internship.rushhour.domain.role.models.RoleGetDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleCreateDTO createRole(RoleCreateDTO roleDTO);
    Page<RoleGetDTO> getPaginated(Pageable pageable);
    Role getRoleEntityById(Long id);
    void deleteRole(Long id);
}
