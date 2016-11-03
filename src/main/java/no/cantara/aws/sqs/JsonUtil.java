package no.cantara.aws.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class JsonUtil {
    static <T extends Object> String from(Map<T, Object> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static Map<Object, Object> toMap(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<HashMap<Object, Object>>() {});
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }
}

