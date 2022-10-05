package com.internship.rushhour.infrastructure.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.internship.rushhour.domain.activity.entity.Activity;

import java.io.IOException;

public class AppointmentActivitySerializer extends JsonSerializer<Activity> {
    @Override
    public void serialize(Activity activity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(activity.getId());
    }
}
