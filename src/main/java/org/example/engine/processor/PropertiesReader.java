package org.example.engine.processor;

import lombok.SneakyThrows;
import org.example.engine.TcpApplication;

import java.util.Properties;

public class PropertiesReader {


    @SneakyThrows
    public String getProperties(String value) {
        Properties properties = new Properties();
        properties.load(TcpApplication.class.getClassLoader().getResourceAsStream("application.properties"));
        return properties.getProperty(value);
    }
}
