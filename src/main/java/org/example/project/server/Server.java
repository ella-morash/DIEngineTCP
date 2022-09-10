package org.example.project.server;

import lombok.SneakyThrows;
import org.example.engine.annotations.*;
import org.example.project.common.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Server {

        @Value("server.port")
        static int PORT;

    @TcpRequestMapping
    public  User receiveRequest(@Payload User request) {
        request.setId(String.valueOf(UUID.randomUUID()));

        System.out.println(request);
        return request;
    }

}



