package org.example.engine;

import lombok.SneakyThrows;
import org.example.engine.annotations.Payload;
import org.example.engine.annotations.TcpRequestMapping;
import org.example.engine.annotations.Value;
import org.example.project.common.model.User;
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
import java.util.Properties;

public class ClassPathDIEngine {

    private final Map<Class<?>,Object> applicationContext = new HashMap<>();
    private final Map<Class<?>,Integer> serverClassToPort = new HashMap<>();
    private EngineObjectMapper engineObjectMapper = new EngineObjectMapper();
    private ServerRunner serverRunner = new ServerRunner();


    public void start() {

        new Reflections("org.example.project")
                .getMethodsAnnotatedWith(TcpRequestMapping.class)
                .forEach(method -> {
                            Class<?> methodClass = method.getClass();
                            Object instance = null;
                            try {
                                instance= methodClass.getDeclaredConstructors()[0].newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            applicationContext.put(methodClass,instance);

                            matchAnnotationValueToClass();

                            var port = serverClassToPort.get(methodClass);




                    String request = null;
                    ServerSocket ss = null;
                    try {
                        ss = new ServerSocket(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (true) {


                        try (

                                Socket socket = ss.accept();
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                        ){

                            request =  (String)ois.readObject();
                            oos.writeObject("CREATED");
                            oos.flush();

                            var objectToSave =  engineObjectMapper.convert(request,User.class);

                            var annotatedParameters  = Arrays.stream(method.getParameters())
                                    .filter(parameter -> parameter.isAnnotationPresent(Payload.class))
                                    .toList();

                            if(!annotatedParameters.isEmpty()) {
                                try {
                                    method.invoke(instance,objectToSave);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }


                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }


                    }

                });

    }

    @SneakyThrows
    private String getProperties(String value) {
        Properties properties = new Properties();
        properties.load(TcpApplication.class.getClassLoader().getResourceAsStream("application.properties"));
        return properties.getProperty(value);
    }

    @SneakyThrows
    private void matchAnnotationValueToClass() {

        applicationContext.entrySet()
                .forEach(classy -> {

                    Arrays.stream(classy.getKey().getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(Value.class))
                            .forEach(field -> {
                                field.setAccessible(true);
                                Value valueAnnotation = field.getAnnotation(Value.class);

                                String dataFromProperties = getProperties(valueAnnotation.value());
                                if (dataFromProperties == null) {
                                    return;
                                }
                                Integer port = Integer.parseInt(dataFromProperties);
                                serverClassToPort.put(field.getClass(),port);


                            });
                });


    }
}
