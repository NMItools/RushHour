package com.internship.rushhour.domain.role.service;

import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.domain.role.models.RoleCreateDTO;
import com.internship.rushhour.domain.role.models.RoleGetDTO;
import com.internship.rushhour.domain.role.repository.RoleRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.ResourceUniqueFieldTakenException;
import com.internship.rushhour.infrastructure.mappers.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper){
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleCreateDTO createRole(RoleCreateDTO roleDTO){
        if(roleRepository.existsByName(roleDTO.getRoleName()))
            throw new ResourceUniqueFieldTakenException(roleDTO.getRoleName(), Role.class.getSimpleName());
        Role role = roleMapper.roleDTOToEntity(roleDTO);
        role.setName(role.getName().toUpperCase(Locale.ROOT));
        return roleMapper.roleEntityToDTO(roleRepository.save(role));
    }

    @Override
    public Page<RoleGetDTO> getPaginated(Pageable pageable) {
        return roleRepository.findAll(pageable).map(roleMapper::roleEntityToGetDTO);
    }

    @Override
    public Role getRoleEntityById(Long id){
        return roleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Role.class.getSimpleName()));
    }

    @Override
    public void deleteRole(Long id) {
        if(!roleRepository.existsById(id)) throw new ResourceNotFoundException(id, "id", Role.class.getSimpleName());
        roleRepository.deleteById(id);
    }
}
