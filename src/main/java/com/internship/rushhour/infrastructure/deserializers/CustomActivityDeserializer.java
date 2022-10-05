package com.internship.rushhour.infrastructure.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.repository.ActivityRepository;
import com.internship.rushhour.domain.employee.repository.EmployeeRepository;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.repository.ProviderRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.fieldaccess.EntityFieldPermissionFactory;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomActivityDeserializer  extends StdDeserializer<Activity> {
    private static ActivityRepository activityRepository;
    private static ProviderRepository providerRepository;
    private static EmployeeRepository employeeRepository;

    @Autowired
    public CustomActivityDeserializer(ActivityRepository activityRepository, ProviderRepository providerRepository,
                                      EmployeeRepository employeeRepository){
        super(Activity.class);
        this.activityRepository = activityRepository;
        this.providerRepository = providerRepository;
        this.employeeRepository = employeeRepository;
    }

    public CustomActivityDeserializer(){super(Activity.class);}

    @Override
    public Activity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode nodes = jsonParser.getCodec().readTree(jsonParser);

        Iterator<String> iterator = nodes.fieldNames();
        List<String> fieldNames = new ArrayList<>();
        iterator.forEachRemaining(e-> fieldNames.add(e));
        String className = Activity.class.getSimpleName();

        String userRole = AuthorizationService.getCurrentUserRole();

        Activity activity = activityRepository.getById(nodes.get("id").asLong());

        for ( String field : fieldNames ) {
            if ( nodes.get(field).asText().equals("null") || field.equals("id") ||EntityFieldPermissionFactory.isLocked(userRole,className+field) ) continue;
            if (field.equals("provider") && !nodes.get(field).asText().isEmpty() ){
                activity.setProvider(providerRepository.findById(nodes.get(field).asLong()).orElseThrow(() ->
                        new ResourceNotFoundException(nodes.get(field).asLong(), "id", Provider.class.getSimpleName())));
            } else if ( field.equals("employees")  ){
                ArrayNode arrayNode = (ArrayNode) nodes.get(field);
                List<Long> employeeIdList = new ArrayList<>();
                arrayNode.elements().forEachRemaining(x -> employeeIdList.add(x.asLong()));

                activity.setEmployees(employeeRepository.findAllById(employeeIdList).stream().
                        filter(x ->
                            x.getProvider().getId().equals(activity.getProvider().getId())
                        ).collect(Collectors.toSet()));
            } else if ( field.equals("price") && !nodes.get("price").asText().equals("0.0")) {
                activity.setPrice((float) nodes.get((field)).asDouble());
            } else if ( field.equals("duration") ){
                activity.setDuration(Duration.ofMinutes(nodes.get(field).asLong()));
            } else if ( field.equals("name") ){
                activity.setName(nodes.get(field).asText());
            }
        }

        return activity;
    }
}
