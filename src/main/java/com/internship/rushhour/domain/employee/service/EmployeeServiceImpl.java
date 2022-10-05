package com.internship.rushhour.domain.employee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.service.AccountService;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeAdminDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTO;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.employee.repository.EmployeeRepository;
import com.internship.rushhour.domain.provider.service.ProviderService;
import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.domain.role.service.RoleService;
import com.internship.rushhour.infrastructure.deserializers.CustomEmployeeDeserializer;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.ResourceUniqueFieldTakenException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.mail.EmailService;
import com.internship.rushhour.infrastructure.mappers.AccountMapper;
import com.internship.rushhour.infrastructure.mappers.EmployeeMapper;
import com.internship.rushhour.infrastructure.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final ProviderService providerService;
    private final AccountMapper accountMapper;
    private final AccountService accountService;
    private final RoleService roleService;
    private final EmailService emailService;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, ProviderService providerService,
                               AccountMapper accountMapper, AccountService accountService, RoleService roleService, EmailService emailService){
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.providerService = providerService;
        this.accountMapper = accountMapper;
        this.accountService = accountService;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public EmployeeDTOResponse create(EmployeeDTO employeeDTO) {
        if ( employeeRepository.existsByAccountEmail(employeeDTO.accountDTO().getEmail())){
            throw new ResourceUniqueFieldTakenException(employeeDTO.accountDTO().getEmail(), Employee.class.getSimpleName());
        }

        Employee e = employeeMapper.dtoToEntity(employeeDTO);
        Account account = accountMapper.accountDTOToEntity(accountService.save(employeeDTO.accountDTO()));
        account.setId(accountService.getByEmail(account.getEmail()).getId());
        e.setAccount(account);

        if(employeeDTO.hireDate() == null) e.setHireDate(LocalDateTime.now());

        if (!account.getRole().getName().equals("EMPLOYEE") &&
                !account.getRole().getName().equals("PROVIDER_ADMINISTRATOR")) throw new UserActionNeededException("Incorrect role selected");

        if ( !verifyEmployeeEmail(e) ){
            throw new UserActionNeededException("Employee email doesn't match their provider's domain. Please change your email");
        }

        e = employeeRepository.save(e);
        emailService.sendAccountCreated(e.getAccount().getEmail(), e.getAccount().getName());

        return employeeMapper.entityToDtoResponse(e);
    }

    @Override
    @Transactional
    public EmployeeDTOResponse createAdminWithProvider(EmployeeAdminDTO employeeAdminDTO){
        String newProviderDomainName = providerService.create(employeeAdminDTO.provider()).businessDomain();

        Role role = roleService.getRoleEntityById(employeeAdminDTO.employee().accountDTO().getRoleId());

        if (!role.getName().equals("PROVIDER_ADMINISTRATOR"))
            throw new UserActionNeededException("""
                    You are trying to create a provider together with its administrator
                    with the wrong roles assigned to the attached account""");
        LocalDateTime hireDate = employeeAdminDTO.employee().hireDate();
        if(hireDate == null) hireDate = LocalDateTime.now();
        EmployeeDTO immutableBypass = new EmployeeDTO(employeeAdminDTO.employee().phone(), providerService.getProviderIdFromBusinessDomain(newProviderDomainName),
                employeeAdminDTO.employee().accountDTO(), employeeAdminDTO.employee().ratePerHour(), employeeAdminDTO.employee().title(),
                hireDate);
        return create(immutableBypass);
    }

    @Override
    public List<String> findAllByHireDate(int month, int day) {
        return employeeRepository.findAllByHireDate(month, day);
    }

    @Override
    public Employee findByEmail(String email) {
        return employeeRepository.findByAccountEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(email, "email", Employee.class.getSimpleName()));
    }



    private boolean verifyEmployeeEmail(Employee e){
        String employeeEmail = e.getAccount().getEmail();
        String employeeEmailDomain = employeeEmail.split("@")[1].split("\\.")[0];

        return (employeeEmailDomain.equals(e.getProvider().getBusinessDomain()));
    }

    @Override
    public EmployeeDTOResponse get(Long id) {
        return employeeMapper.entityToDtoResponse(employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Employee.class.getSimpleName())));
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) throw new ResourceNotFoundException(id, "id", Employee.class.getSimpleName());
        employeeRepository.deleteById(id);
    }

    @Override
    public EmployeeDTO update(JsonPatch patch, Long id) throws JsonPatchException, JsonProcessingException {
        Employee toPatch = employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Employee.class.getSimpleName()));

        Employee e = applyPatchToEmployee(patch, toPatch);
        return employeeMapper.entityToDto(employeeRepository.save(e));
    }

    private Employee applyPatchToEmployee(JsonPatch patch, Employee target)
            throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule simpleModule = new SimpleModule();
        CustomEmployeeDeserializer customEmployeeDeserializer = new CustomEmployeeDeserializer();
        simpleModule.addDeserializer(Employee.class, customEmployeeDeserializer);

        objectMapper.registerModule(simpleModule);

        Employee emptyEmployee = new Employee();
        emptyEmployee.setId(target.getId());

        JsonNode patched = patch.apply(objectMapper.convertValue(emptyEmployee, JsonNode.class));
        return objectMapper.treeToValue(patched, Employee.class);
    }

    @Override
    public Page<EmployeeDTOResponse> getPaginated(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(employeeMapper::entityToDtoResponse);
    }

    @Override
    public Page<EmployeeDTOResponse> getProviderEmployees(Pageable pageable){
        CustomUserDetails currentUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String providerDomain = currentUserDetails.getEmailDomain();

        Long providerId = providerService.getProviderIdFromBusinessDomain(providerDomain);
        List<Employee> employees = employeeRepository.findAllByProviderId(providerId);
        return new PageImpl<>(employees.stream().map(employeeMapper::entityToDtoResponse).collect(Collectors.toList()), pageable, employees.size());
    }

    public Employee getEntity(Long id){
        return employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Employee.class.getSimpleName()));
    }

    @Override
    public List<EmployeeDTOResponse> findAllById(List<Long> id){
        return employeeRepository.findAllById(id).stream().map(employeeMapper::entityToDtoResponse).toList();
    }

    @Override
    public Optional<Employee> findByAccountEmail(String name) {
        return employeeRepository.findByAccountEmail(name);
    }

}
