package com.internship.rushhour.infrastructure.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.internship.rushhour.domain.employee.entity.Employee;

import java.io.IOException;

public class ActivityEmployeeSerializer extends JsonSerializer<Employee> {
    @Override
    public void serialize(Employee employee, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(employee.getId());
    }
}
