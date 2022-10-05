package com.internship.rushhour.infrastructure.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.account.repository.AccountRepository;
import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.domain.role.repository.RoleRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.fieldaccess.EntityFieldPermissionFactory;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import com.internship.rushhour.infrastructure.validators.PasswordConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomAccountDeserializer extends StdDeserializer<Account> {

    private static RoleRepository roleRepository;
    private static AccountRepository accountRepository;
    private static PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAccountDeserializer(RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                                     AccountRepository accountRepository){
        super(Account.class);
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomAccountDeserializer(){
        super(Account.class);
    }

    @Override
    public Account deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode nodes = jsonParser.getCodec().readTree(jsonParser);

        Iterator<String> iterator = nodes.fieldNames();
        List<String> fieldNames = new ArrayList<>();
        iterator.forEachRemaining(fieldNames::add);
        String className = Account.class.getSimpleName();

        String userRole = AuthorizationService.getCurrentUserRole();

        Account account = accountRepository.getById(nodes.get("id").asLong());

        for ( String field : fieldNames ) {
            if ( nodes.get(field).asText().equals("null") || field.equals("id") || EntityFieldPermissionFactory.isLocked(userRole,className+field) ) continue;
            if (field.equals("role") && !nodes.get(field).asText().isEmpty()){
                Role newRole = roleRepository.findById(nodes.get(field).asLong()).orElseThrow(() ->
                        new ResourceNotFoundException(nodes.get(field).asLong(), "id", Role.class.getSimpleName()));
                account.setRole(newRole);
            } else if ( field.equals("email")){
                account.setEmail(nodes.get(field).asText());
            } else if ( field.equals("name")){
                account.setName(nodes.get(field).asText());
            } else if ( field.equals("password")){
                PasswordConstraintValidator validator = new PasswordConstraintValidator();
                if (!validator.isValid(nodes.get(field).asText(), null)){
                    throw new UserActionNeededException("Password must be 8 or more characters in length." +
                            "Password must contain 1 or more uppercase characters." +
                            "Password must contain 1 or more digit characters.,Password must contain 1 or more special characters.");
                }
                account.setPassword(passwordEncoder.encode(nodes.get(field).asText()));
            } else throw new UserActionNeededException("Trying to update account field which does not exist");
        }
        return account;
    }

}
