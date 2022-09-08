package org.example.project.server;

import lombok.SneakyThrows;
import org.example.engine.annotations.Payload;
import org.example.engine.annotations.RunMethod;
import org.example.engine.annotations.TcpRequestMapping;
import org.example.engine.annotations.Value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

        @Value("server.port")
        static int PORT;

    @TcpRequestMapping
    public <T> T receiveRequest(@Payload T request) {


        return request;
    }

}



