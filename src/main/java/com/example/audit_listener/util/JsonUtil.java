package com.example.audit_listener.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * Provides some means to deserialize Json objects.
 */
public final class JsonUtil {

    private JsonUtil() {}

    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }

    public static Object deserialize(String body, Class<?> type) throws IOException {
        return deserialize(body.getBytes(), type);
    }

    public static Object deserialize(byte[] body, Class<?> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return mapper.readValue(body, type);
    }

}
