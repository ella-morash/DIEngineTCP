package org.example.engine;

import lombok.SneakyThrows;
import org.example.engine.annotations.TcpRequestMapping;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClassPathDIEngine {

    private final Map<Class<?>,Object> applicationContext = new HashMap<>();
    private ServerRunner serverRunner = new ServerRunner();


    public void start() {

        new Reflections("org.example.project")
                .getMethodsAnnotatedWith(TcpRequestMapping.class)
                .forEach(method -> {

                    applicationContext.computeIfAbsent(method.getClass(),value-> {
                        Object instance= null;
                        try {
                            instance =  method.getClass().getDeclaredConstructors()[0].newInstance();
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return instance;
                    });

                    injectValueAnnotation();

                    //serverRunner.run();




                        }

                        );
    }

    @SneakyThrows
    private void injectValueAnnotation() {

        Properties properties = new Properties();
        properties.load(TcpApplication.class.getClassLoader().getResourceAsStream("application.properties"));

    }
}
