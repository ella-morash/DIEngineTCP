package org.example.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.project.common.model.User;

import java.io.File;

public class EngineObjectMapper {

    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public  <T> T convert(String requestSrc, Class<T> target) {

        return (T) objectMapper.readValue(requestSrc,target);

    }
}
