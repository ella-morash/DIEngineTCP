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
    public void run(int port) {


        ServerSocket ss = new ServerSocket(port);

        while (true) {
            var socket = ss.accept();


                try (
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                ){

                    var request =  ois.readObject();
                    oos.writeObject("response");
                    oos.flush();

                }
        }
    }
}
