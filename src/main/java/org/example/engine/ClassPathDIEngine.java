package org.example.engine;

import lombok.SneakyThrows;
import org.example.engine.annotations.Payload;
import org.example.engine.annotations.Service;
import org.example.engine.annotations.TcpRequestMapping;
import org.example.engine.annotations.Value;
import org.example.project.common.model.User;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClassPathDIEngine {

    private final Map<Class<?>,Object> applicationContext = new HashMap<>();
    private final Map<Object, Integer> serverClassToPort = new HashMap<>();
    private EngineObjectMapper engineObjectMapper = new EngineObjectMapper();
    private ServerRunner serverRunner = new ServerRunner();
    private PropertiesReader propertiesReader = new PropertiesReader();


    public void start() {

        new Reflections("org.example.project")
                .getTypesAnnotatedWith(Service.class)

                .forEach(service -> {

                    Object instance = null;

                    try {
                        instance = service.getDeclaredConstructors()[0].newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    applicationContext.put(service,instance);

                    matchAnnotationValueToClass();

                    var port = serverClassToPort.get(instance);

                    String request =  (String)serverRunner.receiveRequest(port);

                    Object finalInstance = instance;

                    invokeMethod(service,finalInstance,request);


                });


    }

    private void invokeMethod(Class<?> service, Object finalInstance, String request) {

        Arrays.stream(service.getDeclaredMethods())

                .filter(method -> method.isAnnotationPresent(TcpRequestMapping.class)
                        && Arrays.stream(method.getParameters())
                        .anyMatch(parameter -> parameter.isAnnotationPresent(Payload.class)))

                .forEach(method -> {

                    var paramType = Arrays.stream(method.getParameterTypes())
                           .findFirst().orElse(null);

                    var objectToSave = engineObjectMapper.convert(request,paramType);

                    try {
                        method.invoke(finalInstance,objectToSave);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
              });
    }


    @SneakyThrows
    private void matchAnnotationValueToClass() {

        applicationContext.entrySet()
                .forEach(classy -> {

                    Arrays.stream(classy.getKey().getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(Value.class))
                            .forEach(field -> {

                                Value valueAnnotation = field.getAnnotation(Value.class);

                                String dataFromProperties = propertiesReader.getProperties(valueAnnotation.value());
                                if (dataFromProperties == null) {
                                    return;
                                }
                                Integer port = Integer.parseInt(dataFromProperties);
                                serverClassToPort.put(classy.getValue(),port);


                            });
                });


    }
}
