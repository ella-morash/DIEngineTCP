package org.example.engine.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;


public class EngineObjectMapper {

    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public  <T> T convert(String requestSrc, Class<T> target) {

        return (T) objectMapper.readValue(requestSrc,target);

    }
}
