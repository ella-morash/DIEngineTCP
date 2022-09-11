package org.example.project.server;


import org.example.engine.annotations.*;
import org.example.project.common.model.User;


import java.util.UUID;


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



