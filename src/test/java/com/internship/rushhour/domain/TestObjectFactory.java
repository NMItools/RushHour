package com.internship.rushhour.domain;

import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.models.AccountDTO;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.models.ClientDTO;
import com.internship.rushhour.domain.client.models.ClientDTOResponse;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.models.EmployeeDTOResponse;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import com.internship.rushhour.domain.role.entity.Role;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


public class TestObjectFactory {
    private static final Random random = new Random();

    public static Provider generateRandomisedProvider(){
        Provider p = new Provider();
        p.setId(random.nextLong(100000)+1);
        p.setWebsite("website"+generateRandomCharSequence(5)+".com");
        p.setPhone(generateRandomNumberSequence(10));
        p.setName("name" + generateRandomCharSequence(4));
        p.setBusinessDomain(".com");
        p.setBusinessHoursStart(LocalTime.parse("06:00"));
        p.setBusinessHoursEnd(LocalTime.parse("22:00"));

        Set<DayOfWeek> testWorkingDays = new HashSet<>();
        testWorkingDays.add(DayOfWeek.valueOf("MONDAY"));
        testWorkingDays.add(DayOfWeek.valueOf("FRIDAY"));
        testWorkingDays.add(DayOfWeek.valueOf("TUESDAY"));
        testWorkingDays.add(DayOfWeek.valueOf("WEDNESDAY"));

        p.setWorkingDays(testWorkingDays);
        return p;
    }

    public static Appointment generateAppointment(){
        Appointment a = new Appointment();
        a.setStartTime(LocalDateTime.of(9999, 6,22, 10, 0).plusHours(1L));
        Set<Activity> activities = new HashSet<>();
        activities.add(generateRandomisedActivity());
        activities.add(generateRandomisedActivity());
        a.setActivities(activities);
        a.setEmployee(generateRandomisedEmployee());
        a.populateEndDate();
        a.populatePrice();
        a.setClient(generateRandomisedClient());
        return a;
    }

    public static AppointmentResponseDTO generateAppointmentResponseDTO(){
        Appointment a = generateAppointment();
        List<ActivityResponseDTO> activityResponseDTOList = new ArrayList<>();
        activityResponseDTOList.add(generateActivityResponseDto());
        activityResponseDTOList.add(generateActivityResponseDto());
        activityResponseDTOList.add(generateActivityResponseDto());
        return new AppointmentResponseDTO(a.getId(), a.getStartTime(), a.getEndDate(), generateEmployeeDTOResponse(), generateClientDto(), random.nextFloat(100f)+1f,activityResponseDTOList);
    }

    public static ProviderDTO generateProviderDto(){
        Provider p = generateRandomisedProvider();
        return new ProviderDTO(p.getName(),
                p.getWebsite(), p.getBusinessDomain(), p.getPhone(), p.getBusinessHoursStart(), p.getBusinessHoursEnd(), p.getWorkingDays().stream().map(x -> x.name()).collect(Collectors.toSet()));
    }

    public static ProviderDTOResponse generateProviderDtoResponse(Provider p){
        return new ProviderDTOResponse(p.getId(),p.getName(),
                p.getWebsite(), p.getBusinessDomain(), p.getPhone(), p.getBusinessHoursStart(), p.getBusinessHoursEnd(), p.getWorkingDays().stream().map(Enum::name).collect(Collectors.toSet()));
    }

    public static AppointmentDTO generateAppointmentDto(){
        Appointment a = generateAppointment();
        return new AppointmentDTO(a.getStartTime(), a.getEmployee().getId(), a.getClient().getId(),
                a.getActivities().stream().map(x -> x.getId()).collect(Collectors.toList()), false, false, false, false);
    }

    public static ClientDTO generateClientDto(){
        Client c = generateRandomisedClient();
        return new ClientDTO(c.getPhone(), c.getAddress(), generateAccountDto());
    }

    public static ClientDTOResponse generateClientDtoResponse(Client c){
        return new ClientDTOResponse(c.getId(), c.getPhone(), c.getAddress(), generateAccountDto());
    }

    public static Account generateRandomisedAccount(){
        Account a = new Account();
        a.setName("name"+generateRandomCharSequence(2));
        a.setRole(new Role("TEST_ROLE"));
        a.setPassword("pass"+generateRandomCharSequence(3));
        a.setEmail(generateRandomCharSequence(5)+"@"+generateRandomCharSequence(3)+".com");
        a.setId(random.nextLong(1000000)+1);
        return a;
    }

    public static EmployeeDTOResponse generateEmployeeDTOResponse(){
        Account a = generateRandomisedAccount();
        Employee e = generateRandomisedEmployee();
        return new EmployeeDTOResponse(e.getId(),e.getPhone(), e.getProvider().getName(), a.getName(), a.getEmail(), e.getRatePerHour(), e.getTitle(), null);
    }

    public static EmployeeDTOResponse generateEmployeeDTOResponse( Employee e){
        return new EmployeeDTOResponse(e.getId(),e.getPhone(), e.getProvider().getName(), e.getAccount().getName(),  e.getAccount().getEmail(), e.getRatePerHour(), e.getTitle(), null);
    }

    public static Employee generateRandomisedEmployee(){
        Employee e = new Employee();
        e.setAccount(generateRandomisedAccount());
        e.setProvider(generateRandomisedProvider());
        e.setPhone(generateRandomNumberSequence(10));
        e.setRatePerHour(5.0f);
        e.setTitle("Mr");
        e.setId(random.nextLong(10000L)+1L);
        return e;
    }

    public static EmployeeDTOResponse generateRandomEmployeeResponseDto(){
        return new EmployeeDTOResponse(random.nextLong(20000)+1,generateRandomNumberSequence(9), generateRandomCharSequence(10),generateRandomCharSequence(10), generateRandomCharSequence(10), random.nextFloat(20f)+1f, generateRandomCharSequence(10), null);
    }

    public static Client generateRandomisedClient(){
        Client c = new Client();
        c.setAccount(generateRandomisedAccount());
        c.setPhone(generateRandomNumberSequence(10));
        c.setAddress("address"+generateRandomCharSequence(5));
        c.setId(random.nextLong(100000)+1);
        return c;
    }

    public static Activity generateRandomisedActivity(){
        Activity a = new Activity();
        a.setId(random.nextLong(2000+1));
        a.setPrice(random.nextFloat(100.0f)+1.0f);
        a.setDuration(Duration.ofMinutes(random.nextLong(60)+1));
        a.setProvider(generateRandomisedProvider());
        Set<Employee> employees= new HashSet<>();
        employees.add(generateRandomisedEmployee());
        employees.add(generateRandomisedEmployee());
        employees.add(generateRandomisedEmployee());
        a.setEmployees(employees);
        return a;
    }

    public static ActivityResponseDTO generateActivityResponseDto(){
        ProviderDTO pdto = generateProviderDto();
        List<EmployeeDTOResponse> list = new ArrayList<>();
        list.add(generateRandomEmployeeResponseDto());
        list.add(generateRandomEmployeeResponseDto());
        return new ActivityResponseDTO(1l,"activityname", random.nextFloat(10f)+1f, Duration.ofMinutes(random.nextLong(60)+1),pdto,list );
    }

    public static ActivityDTO generateActivityDto(){
        List<Long> ids = new ArrayList<>();
        for (int i = 0;i< (random.nextInt(30)+2);i++){
            ids.add(random.nextLong(20000)+1);
        }
        return new ActivityDTO(random.nextFloat(50f)+1f, Duration.ofMinutes(random.nextLong(60)+1), random.nextLong(1000)+1, ids, "activityname");
    }

    public static AccountDTO generateAccountDto(){
        return new AccountDTO("accdtoemail@gmail.com", "accdtoNAME", "WhyPass@@4", 1L);
    }

    public static AccountDTO generateAccountDto(Account a){
        return new AccountDTO(a.getEmail(), a.getName(), a.getPassword(),1L);
    }

    private static String generateRandomCharSequence(int length){
        String result = "";
        for ( int i = 0 ; i < length ; i++ ){
            char c = (char)(random.nextInt(26) + 'a');
            result = result + c;
        }
        return result;
    }

    private static String generateRandomNumberSequence(int length){
        String result = "";
        for ( int i = 0 ; i < length ; i++ ){
            result += random.nextInt(10);
        }
        return result;
    }
}
