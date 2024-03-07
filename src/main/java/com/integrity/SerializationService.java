package com.integrity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationService {

    // Serialize the object into JSON
    public static String serializeToJson(Object instance) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(instance);
    }

    // Deserialize JSON into an instance of MyClass
    public static LoadableClass deserializeFromJson(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, LoadableClass.class);
    }

}
