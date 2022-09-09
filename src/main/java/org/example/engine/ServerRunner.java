package org.example.engine;

import lombok.SneakyThrows;
import org.example.engine.annotations.RunMethod;
import org.example.engine.annotations.Value;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRunner {



    @SneakyThrows
    public Object receiveRequest(int port) {


        ServerSocket ss = new ServerSocket(port);
        Object request = null;


            var socket = ss.accept();


                try (
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                ){

                    request =  ois.readObject();
                    oos.writeObject("CREATED");
                    oos.flush();

                }
        return request;
    }
}
