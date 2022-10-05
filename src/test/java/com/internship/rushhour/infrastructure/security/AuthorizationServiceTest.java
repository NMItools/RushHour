package com.internship.rushhour.infrastructure.security;

import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.activity.service.ActivityService;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.appointment.service.AppointmentService;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import com.internship.rushhour.domain.client.service.ClientService;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.employee.service.EmployeeService;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.service.ProviderService;
import com.internship.rushhour.domain.role.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    Role providerAdministrator;
    Role employee;
    Role client;

    @Mock
    ClientService clientService;

    @Mock
    EmployeeService employeeService;

    @Mock
    ProviderService providerService;

    @Mock
    ActivityService activityService;

    @Mock
    AppointmentService appointmentService;

    @InjectMocks
    AuthorizationService authorizationService;

    String emailTrue = "123@test.com";
    String emailFalse = "zzz@bzzz.com";
    Account account;
    CustomUserDetails user;

    @BeforeEach
    void beforeEach(){
        providerAdministrator = new Role(1L, "PROVIDER_ADMINISTRATOR");
        employee = new Role(2L, "EMPLOYEE");
        client = new Role(3L, "CLIENT");
    }

    @Test
    void isOwnerOfClientTrue() {
        setAccountAndUserTrue(client);

        when(clientService.get(anyLong())).thenReturn(getClientDtoResponseWithAccount());

        assertThat(authorizationService.isOwnerOfClient(5L, user)).isTrue();
    }

    @Test
    void isOwnerOfClientFalse() {
        setAccountAndUserDifferent(client);

        when(clientService.get(anyLong())).thenReturn(getClientDtoResponseWithAccount());

        assertThat(authorizationService.isOwnerOfClient(5L, user)).isFalse();
    }

    @Test
    void isOwnerOfEmployeeTrue() {
        setAccountAndUserTrue(employee);

        when(employeeService.get(anyLong())).thenReturn(getEmployeeDtoResponseWithAccount());

        assertThat(authorizationService.isOwnerOfEmployee(1L, user)).isTrue();
    }

    @Test
    void isOwnerOfEmployeeRoleFalse() {
        setAccountAndUserTrue(providerAdministrator);

        when(employeeService.get(anyLong())).thenReturn(getEmployeeDtoResponseWithAccount());

        assertThat(authorizationService.isOwnerOfEmployee(1L, user)).isFalse();
    }

    @Test
    void isOwnerOfEmployeeEmailFalse() {
        setAccountAndUserDifferent(employee);

        when(employeeService.get(anyLong())).thenReturn(getEmployeeDtoResponseWithAccount());

        assertThat(authorizationService.isOwnerOfEmployee(1L, user)).isFalse();
    }

    @Test
    void isOwnerOfEmployeeProviderTrue() {
        setAccountAndUserTrue(providerAdministrator);
        Provider p = getProviderWithDomain("test");
        Employee e = getEmployeeWithAccountAndProvider(p);

        lenient().when(employeeService.getEntity(anyLong())).thenReturn(e);
        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.isOwnerOfEmployeeProvider(5L, user)).isTrue();
    }

    @Test
    void isOwnerOfEmployeeProviderFalseRole() {
        setAccountAndUserTrue(client);
        Provider p = getProviderWithDomain("test");
        Employee e = getEmployeeWithAccountAndProvider(p);

        lenient().when(employeeService.getEntity(anyLong())).thenReturn(e);
        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.isOwnerOfEmployeeProvider(5L, user)).isFalse();
    }

    @Test
    void isOwnerOfEmployeeProviderFalseEmail() {
        setAccountAndUserTrue(providerAdministrator);
        Provider p = getProviderWithDomain("raaa");
        Employee e = getEmployeeWithAccountAndProvider(p);

        lenient().when(employeeService.getEntity(anyLong())).thenReturn(e);
        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.isOwnerOfEmployeeProvider(5L, user)).isFalse();
    }

    @Test
    void hasAccessToEmployeeTrue() {
        setAccountAndUserTrue(employee);
        Provider p = getProviderWithDomain("raaaa");
        Employee e = getEmployeeWithAccountAndProvider(p);

        lenient().when(employeeService.getEntity(anyLong())).thenReturn(e);
        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.hasAccessToEmployee(5l, user)).isTrue();
    }

    @Test
    void hasAccessToEmployeeFalseRole() {
        setAccountAndUserTrue(client);
        Provider p = getProviderWithDomain("raaaa");
        Employee e = getEmployeeWithAccountAndProvider(p);

        lenient().when(employeeService.getEntity(anyLong())).thenReturn(e);
        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.hasAccessToEmployee(5l, user)).isFalse();
    }

    @Test
    void hasAccessToEmployeeFalseEmail() {
        setAccountAndUserDifferent(client);
        Provider p = getProviderWithDomain("raaaa");
        Employee e = getEmployeeWithAccountAndProvider(p);

        lenient().when(employeeService.getEntity(anyLong())).thenReturn(e);
        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.hasAccessToEmployee(5l, user)).isFalse();
    }

    @Test
    void isAdministratorOfProviderTrue() {
        setAccountAndUserTrue(providerAdministrator);
        Provider p = getProviderWithDomain("test");

        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.isAdministratorOfProvider(5l, user)).isTrue();
    }

    @Test
    void isAdministratorOfProviderFalseRole() {
        setAccountAndUserTrue(client);
        Provider p = getProviderWithDomain("test");

        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.isAdministratorOfProvider(5l, user)).isFalse();
    }

    @Test
    void isAdministratorOfProviderFalseDomain() {
        setAccountAndUserTrue(client);
        Provider p = getProviderWithDomain("bzzzzzzzzz");

        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);

        assertThat(authorizationService.isAdministratorOfProvider(5l, user)).isFalse();
    }
    @Test
    void canCRUDActivityTrue() {
        setAccountAndUserTrue(providerAdministrator);
        Provider p = getProviderWithDomain("test");
        Activity activity = new Activity();
        activity.setProvider(p);

        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);
        lenient().when(activityService.getEntity(anyLong())).thenReturn(activity);

        assertThat(authorizationService.canCRUDActivity(23L, user)).isTrue();
    }

    @Test
    void canCRUDActivityFalse() {
        setAccountAndUserTrue(client);
        Provider p = getProviderWithDomain("bzzzzzzzzzzzzz");
        Activity activity = new Activity();
        activity.setProvider(p);

        lenient().when(providerService.getEntity(anyLong())).thenReturn(p);
        lenient().when(activityService.getEntity(anyLong())).thenReturn(activity);

        assertThat(authorizationService.canCRUDActivity(23L, user)).isFalse();
    }

    @Test
    void canCreateProviderTrue() {
        setAccountAndUserTrue(providerAdministrator);
        String providerBusinessDomain = "test";

        assertThat(authorizationService.canCreateProvider(providerBusinessDomain, user)).isTrue();
    }

    @Test
    void canCreateProviderFalse() {
        setAccountAndUserTrue(providerAdministrator);
        String providerBusinessDomain = "BRRRRR";

        assertThat(authorizationService.canCreateProvider(providerBusinessDomain, user)).isFalse();
    }

    @Test
    void isPartOfAppointmentTrue() {
        setAccountAndUserTrue(providerAdministrator);
        ClientDTO clientDTO = new ClientDTO("", "", TestObjectFactory.generateAccountDto(account));
        EmployeeDTOResponse employeeResponse =getEmployeeDtoResponseWithAccount();
        ProviderDTO providerDTO = new ProviderDTO(null, null, "test", null, null, null, null);
        ActivityResponseDTO activityResponse = new ActivityResponseDTO(1l,null,0.0f, null, providerDTO, null);
        List<ActivityResponseDTO> li = new ArrayList<>();
        li.add(activityResponse);
        AppointmentResponseDTO appointmentResponse = new AppointmentResponseDTO(null, null, null, employeeResponse, clientDTO, 0.0f, li);

        when(appointmentService.get(any())).thenReturn(appointmentResponse);

        assertThat(authorizationService.isPartOfAppointment(50L, user)).isTrue();
    }

    @Test
    void isPartOfAppointmentFalseEmail() {
        setAccountAndUserDifferent(providerAdministrator);
        ClientDTO clientDTO = new ClientDTO("", "", TestObjectFactory.generateAccountDto(account));
        EmployeeDTOResponse employeeResponse = getEmployeeDtoResponseWithAccount();
        ProviderDTO providerDTO = new ProviderDTO(null, null, "asd", null, null, null, null);
        ActivityResponseDTO activityResponse = new ActivityResponseDTO(1l,null,0.0f, null, providerDTO, null);
        List<ActivityResponseDTO> li = new ArrayList<>();
        li.add(activityResponse);
        AppointmentResponseDTO appointmentResponse = new AppointmentResponseDTO(null,null, null, employeeResponse, clientDTO, 0.0f, li);

        when(appointmentService.get(any())).thenReturn(appointmentResponse);

        assertThat(authorizationService.isPartOfAppointment(50L, user)).isFalse();
    }

    private Account getTestAccount(String email, Role role){
        return new Account(1L, email, "test name", "nopass", role);
    }

    private void setAccountAndUserTrue(Role r){
        account = getTestAccount("123@test.com", r);
        user = CustomUserDetails.create(account);
    }

    private void setAccountAndUserDifferent(Role r){
        account = getTestAccount("123@test.com", r);
        user = CustomUserDetails.create(getTestAccount(emailFalse, r));
    }

    private ClientDTOResponse getClientDtoResponseWithAccount(){
        return new ClientDTOResponse(1L, "1234567891", "address", TestObjectFactory.generateAccountDto(account));
    }

    private EmployeeDTOResponse getEmployeeDtoResponseWithAccount(){
        return new EmployeeDTOResponse(1L, "1234567891", "PROVIDERNAME", "EMPLOYEENAME", account.getEmail(), 5.5f, "TITLE", null);
    }

    private Provider getProviderWithDomain(String domain){
        Provider p = new Provider();
        p.setId(1L);
        p.setBusinessDomain(domain);
        return p;
    }

    private Employee getEmployeeWithAccountAndProvider(Provider p){
        return new Employee(null, null, p, account, 0.0f, null, null);
    }
}