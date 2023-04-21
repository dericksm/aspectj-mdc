package com.github.dericksm.aspectjmdc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.dericksm.aspectjmdc.aspectj.Sensitive;
import java.io.IOException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemSerializer extends StdSerializer<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemSerializer.class);

    public ItemSerializer() {
        this(null);
    }

    public ItemSerializer(Class<Object> t) {
        super(t);
    }

    @Override
    public void serialize(final Object value, final JsonGenerator jsonGenerator, final SerializerProvider provider)
        throws IOException {

        jsonGenerator.writeStartObject();
        var fields = value.getClass().getDeclaredFields();
        try {
            for (var field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Sensitive.class)) {
                    jsonGenerator.writeStringField(field.getName(),
                        Base64.getEncoder().encodeToString(String.valueOf(field.get(value)).getBytes()));
                } else {
                    jsonGenerator.writeStringField(field.getName(), String.valueOf(field.get(value)));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Could not serialize object {}", value.getClass().getName());
        }
    }
}