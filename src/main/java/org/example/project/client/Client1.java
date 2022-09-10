package org.example.project.client;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client1 {
    static final String SERVER_HOST = "localhost";
    static final int PORT = 8080;


    @SneakyThrows
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(SERVER_HOST, PORT);

        try (
                ObjectInputStream socketInput = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream socketOutput = new ObjectOutputStream(socket.getOutputStream());

        ) {

            String request = "{\"firstName\":\"Alex\",\"lastName\":\"Turner\"}";


            socketOutput.writeObject(request);
            socketOutput.flush();
            var response = socketInput.readObject();

        }
    }
}
