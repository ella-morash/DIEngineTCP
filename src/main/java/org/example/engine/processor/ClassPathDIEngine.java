package org.example.engine.processor;

import lombok.SneakyThrows;
import org.example.engine.annotations.Payload;
import org.example.engine.annotations.Service;
import org.example.engine.annotations.TcpRequestMapping;
import org.example.engine.annotations.Value;

import org.reflections.Reflections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ClassPathDIEngine {

    private final Map<Class<?>,Object> applicationContext = new HashMap<>();
    private final Map<Object, Integer> serverInstanceToPort = new HashMap<>();





    public synchronized void start() {

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

                    var port = serverInstanceToPort.get(instance);


                    ServerSocket ss = null;
                    try {
                        ss = new ServerSocket(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String request = null;

                    while (true) {

                        try (
                                Socket socket = ss.accept();
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                        ){

                            request = (String) ois.readObject();
                            oos.writeObject("CREATED");
                            oos.flush();

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        invokeMethod(service, instance,request);

                    }


                });


    }

    private void invokeMethod(Class<?> service, Object finalInstance, String request) {

        EngineObjectMapper engineObjectMapper = new EngineObjectMapper();

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

        PropertiesReader propertiesReader = new PropertiesReader();
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
                                serverInstanceToPort.put(classy.getValue(),port);


                            });
                });


    }
}
