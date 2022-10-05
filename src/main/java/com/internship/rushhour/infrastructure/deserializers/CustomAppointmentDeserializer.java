package com.internship.rushhour.infrastructure.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.repository.ActivityRepository;
import com.internship.rushhour.domain.activity.service.ActivityService;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.appointment.repository.AppointmentRepository;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.repository.ClientRepository;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.repository.EmployeeRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.fieldaccess.EntityFieldPermissionFactory;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAppointmentDeserializer extends StdDeserializer<Appointment> {
    private static AppointmentRepository appointmentRepository;
    private static EmployeeRepository employeeRepository;
    private static ClientRepository clientRepository;
    private static ActivityService activityService;

    @Autowired
    public CustomAppointmentDeserializer(AppointmentRepository appointmentRepository, EmployeeRepository employeeRepository,
                                         ClientRepository clientRepository, ActivityService activityService){
        super(Appointment.class);
        this.appointmentRepository = appointmentRepository;
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
        this.activityService = activityService;
    }

    public CustomAppointmentDeserializer() {super(Appointment.class);}

    @Override
    public Appointment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode nodes = jsonParser.getCodec().readTree(jsonParser);

        Iterator<String> iterator = nodes.fieldNames();
        List<String> fieldNames = new ArrayList<>();
        iterator.forEachRemaining(e-> fieldNames.add(e));
        String className = Appointment.class.getSimpleName();

        String userRole = AuthorizationService.getCurrentUserRole();

        Appointment appointment = appointmentRepository.getById(nodes.get("id").asLong());

        for ( String field : fieldNames ) {
            if ( nodes.get(field).asText().equals("null") || field.equals("id") || nodes.get(field).asText().equals("0.0") ||EntityFieldPermissionFactory.isLocked(userRole,className+field) ) continue;
            if (field.equals("employee") && !nodes.get(field).asText().isEmpty() ){
                appointment.setEmployee(employeeRepository.findById(nodes.get(field).asLong()).orElseThrow(() ->
                        new ResourceNotFoundException(nodes.get(field).asLong(), "id", Employee.class.getSimpleName())));
            } else if (field.equals("client") && !nodes.get(field).asText().isEmpty()) {
                    appointment.setClient(clientRepository.findById(nodes.get(field).asLong()).orElseThrow(() ->
                            new ResourceNotFoundException(nodes.get(field).asLong(), "id", Client.class.getSimpleName())));
            } else if ( field.equals("activities")  ){
                ArrayNode arrayNode = (ArrayNode) nodes.get(field);
                List<Long> activityIdList = new ArrayList<>();
                arrayNode.elements().forEachRemaining(x -> activityIdList.add(x.asLong()));

                if(activityIdList.isEmpty()) throw new UserActionNeededException("Can not set appointment activities to zero.");

                List<Activity> activities = activityService.findAllById(activityIdList);
                if(activities.isEmpty()) throw new UserActionNeededException("No valid activities specified.");

                for ( Activity a : activities ){
                    if (!a.getProvider().getId().equals(appointment.getEmployee().getProvider().getId())) {
                        throw new UserActionNeededException("Can not give appointment an activity which the employee is not authorized to do.");
                    }
                }

                appointment.setActivities(activities.stream().collect(Collectors.toSet()));
            } else if ( field.equals("startTime") ) {
                appointment.setStartTime(LocalDateTime.parse(nodes.get(field).asText().replace("@", "T"))); // maybe decimal issues?
            }
        }

        appointment.populateEndDate();
        appointment.populatePrice();
        return appointment;
    }
}
