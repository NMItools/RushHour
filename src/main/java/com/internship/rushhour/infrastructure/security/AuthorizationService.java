package com.internship.rushhour.infrastructure.security;

import com.internship.rushhour.domain.activity.service.ActivityService;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.appointment.service.AppointmentService;
import com.internship.rushhour.domain.client.service.ClientService;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.service.EmployeeService;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.service.ProviderService;
import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.domain.role.models.Roles;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service(value = "autho")
public class AuthorizationService {
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ProviderService providerService;
    private final ActivityService activityService;
    private final AppointmentService appointmentService;

    private static final String ROLE_EMPLOYEE = Roles.ROLE_EMPLOYEE.name();
    private static final String ROLE_PROVIDER_ADMINISTRATOR = Roles.ROLE_PROVIDER_ADMINISTRATOR.name();
    private static final String ROLE_CLIENT = Roles.ROLE_CLIENT.name();

    @Autowired
    public AuthorizationService(ClientService clientService, EmployeeService employeeService,
                       ProviderService providerService, ActivityService activityService,
                                AppointmentService appointmentService){

        this.clientService = clientService;
        this.employeeService = employeeService;
        this.providerService = providerService;
        this.activityService = activityService;
        this.appointmentService = appointmentService;
    }

    public boolean isOwnerOfClient(Long clientId, CustomUserDetails user){
        return (clientService.get(clientId).accountDTO().getEmail().equals(user.getUsername()) && isClient(user));
    }

    public boolean isOwnerOfEmployee(Long employeeId, CustomUserDetails user){
        return (employeeService.get(employeeId).email().equals(user.getUsername()) && isEmployee(user));
    }

    public boolean isOwnerOfEmployeeProvider(Long employeeId, CustomUserDetails user){
        return isAdministratorOfProvider(employeeService.getEntity(employeeId).getProvider().getId(), user);
    }

    public boolean hasAccessToEmployee(Long id, CustomUserDetails user){
        Employee e = employeeService.getEntity(id);

        boolean rolesMatch = (isEmployee(user) || isProviderAdministrator(user));
        boolean hasAccess = (e.getAccount().getEmail().equals(user.getUsername()) || isAdministratorOfProvider(e.getProvider().getId(), user));
        return (rolesMatch && hasAccess);
    }

    public boolean isAdministratorOfProvider(Long providerId, CustomUserDetails user){
        String userEmail = user.getUsername();
        Provider provider = providerService.getEntity(providerId);

        return (  administratorEmailValid(userEmail,provider.getBusinessDomain()) && isProviderAdministrator(user));
    }

    public boolean canCRUDActivity(Long activityId, CustomUserDetails user){
        return isAdministratorOfProvider(activityService.getEntity(activityId).getProvider().getId(), user);
    }

    public boolean canCreateProvider(String providerBusinessDomain, CustomUserDetails user){
        boolean isProviderAdmin = getCurrentUserRole(user).equals(ROLE_PROVIDER_ADMINISTRATOR);
        boolean belongsToProvider = providerBusinessDomain.equals(user.getEmailDomain());
        return isProviderAdmin && belongsToProvider;
    }

    public boolean isPartOfAppointment(Long appointmentId, CustomUserDetails user){
        AppointmentResponseDTO appointment = appointmentService.get(appointmentId);
        boolean employeeOwnsAppointment = appointment.employee().email().equals(user.getUsername());
        boolean providerAdminOfOwner = appointment.activities().get(0).providerDto().businessDomain().equals(user.getEmailDomain());
        boolean isClientOfAppointment = appointment.client().accountDTO().getEmail().equals(user.getUsername());

        return (employeeOwnsAppointment || providerAdminOfOwner || isClientOfAppointment);
    }

    private boolean administratorEmailValid(String email, String bussinessDomain){
        String userEmailDomain = email.split("@")[1].split("\\.")[0];
        return userEmailDomain.equals(bussinessDomain);
    }

    private String getCurrentUserRole(CustomUserDetails user){
        return user.getAuthorities().stream().findFirst().orElseThrow(()->
                new ResourceNotFoundException("EMPTY", "User Authority", Role.class.getSimpleName())).getAuthority();
    }

    public static String getCurrentUserRole(){
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getAuthorities().stream().findFirst().orElseThrow(()->
                new ResourceNotFoundException("EMPTY", "User Authority", Role.class.getSimpleName())).getAuthority();
    }

    private boolean isProviderAdministrator(CustomUserDetails user){
        return getCurrentUserRole(user).equals(ROLE_PROVIDER_ADMINISTRATOR);
    }

    private boolean isClient(CustomUserDetails user){
        return getCurrentUserRole(user).equals(ROLE_CLIENT);
    }

    private boolean isEmployee(CustomUserDetails user){
        return getCurrentUserRole(user).equals(ROLE_EMPLOYEE);
    }
}
