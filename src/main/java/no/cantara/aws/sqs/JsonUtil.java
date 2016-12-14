package no.cantara.aws.sqs;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static <T extends Object> String from(Map<T, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static Map<Object, Object> toMap(String json) {
        try {
            if (StringUtils.isNullOrEmpty(json)) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(json, TypeFactory.defaultInstance().constructMapType(HashMap.class, Object.class, Object.class));
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }
}

