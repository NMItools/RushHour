package com.internship.rushhour.infrastructure.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.repository.ClientRepository;
import com.internship.rushhour.infrastructure.fieldaccess.EntityFieldPermissionFactory;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class CustomClientDeserializer extends StdDeserializer<Client> {
    private static ClientRepository clientRepository;

    @Autowired
    public CustomClientDeserializer(ClientRepository clientRepository){
        super(Client.class);
        CustomClientDeserializer.clientRepository = clientRepository;
    }

    public CustomClientDeserializer(){
        super(Client.class);
    }

    @Override
    public Client deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode nodes = jsonParser.getCodec().readTree(jsonParser);

        Iterator<String> iterator = nodes.fieldNames();
        List<String> fieldNames = new ArrayList<>();
        iterator.forEachRemaining(e-> fieldNames.add(e));
        String className = Client.class.getSimpleName();

        String userRole = AuthorizationService.getCurrentUserRole();

        Client client = clientRepository.getById(nodes.get("id").asLong());
        Account a = client.getAccount();

        for ( String field : fieldNames ) {
            if ( nodes.get(field).asText().equals("null") || field.equals("id") || EntityFieldPermissionFactory.isLocked(userRole,className+field) ) continue;
            if (field.equals("phone") && !nodes.get(field).asText().isEmpty() ){
                client.setPhone(nodes.get(field).asText());
            } else if (field.equals("address")){
                client.setAddress(nodes.get(field).asText());
            } else if (field.equals("account") ){
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
                client.setAccount(a);
            }
        }

        return client;
    }
}
