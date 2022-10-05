package com.internship.rushhour.infrastructure.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.repository.AccountRepository;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.repository.EmployeeRepository;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.repository.ProviderRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.fieldaccess.EntityFieldPermissionFactory;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomEmployeeDeserializer extends StdDeserializer<Employee> {
    private static AccountRepository accountRepository;
    private static ProviderRepository providerRepository;
    private static EmployeeRepository employeeRepository;

    @Autowired
    public CustomEmployeeDeserializer(AccountRepository accountRepository, ProviderRepository providerRepository,
                                      EmployeeRepository employeeRepository){
        super(Employee.class);
        this.accountRepository = accountRepository;
        this.providerRepository = providerRepository;
        this.employeeRepository = employeeRepository;
    }

    public CustomEmployeeDeserializer(){super(Employee.class); }

    @Override
    public Employee deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode nodes = jsonParser.getCodec().readTree(jsonParser);


        Iterator<String> iterator = nodes.fieldNames();
        List<String> fieldNames = new ArrayList<>();
        iterator.forEachRemaining(e-> fieldNames.add(e));
        String className = Employee.class.getSimpleName();

        String userRole = AuthorizationService.getCurrentUserRole();

        Employee e = employeeRepository.getById(nodes.get("id").asLong());
        Account a = e.getAccount();

        for ( String field : fieldNames ) {
            if ( nodes.get(field).asText().equals("null") || field.equals("id") ||EntityFieldPermissionFactory.isLocked(userRole,className+field) ) continue;
            if (field.equals("account") ){
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule simpleModule = new SimpleModule();

                simpleModule.addDeserializer(Account.class, new CustomAccountDeserializer());
                objectMapper.registerModule(simpleModule);

                Account emptyAccount = new Account();
                emptyAccount.setId(a.getId());
                JsonNode accToNode = objectMapper.convertValue(emptyAccount, JsonNode.class);

                nodes.get(field).fieldNames().forEachRemaining(f -> {
                    ((ObjectNode)accToNode).set(f, nodes.get(field).get(f));
                });

                a = objectMapper.treeToValue(accToNode, Account.class);
                e.setAccount(a);
            } else if ( field.equals("provider") && !nodes.get("provider").asText().isEmpty() ){
                e.setProvider(providerRepository.findById(nodes.get(field).asLong()).orElseThrow(() ->
                        new ResourceNotFoundException(nodes.get(field).asLong(), "id", Provider.class.getSimpleName())));
            } else if ( field.equals("phone") ) {
                if (!Pattern.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$"
                        , nodes.get(field).asText())) throw new UserActionNeededException("Please enter a valid phone number");
                e.setPhone(nodes.get((field)).asText());
            } else if ( field.equals("title") ){
                e.setTitle(nodes.get(field).asText());
            } else if ( field.equals("ratePerHour") && !nodes.get("ratePerHour").asText().equals("0.0")){
                try {
                    e.setRatePerHour(Float.parseFloat(nodes.get(field).asText()));
                } catch (Exception ex ){
                    throw new UserActionNeededException("Invalid rate per hour: " + ex.getMessage());
                }
            }
        }

        return e;
    }

}
