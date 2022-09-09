package org.example.project.client;

import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;

public class Client {

    static final String SERVER_HOST = "localhost";
    static final int PORT = 8080;


    @SneakyThrows
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(SERVER_HOST, PORT);

        try(
                ObjectInputStream socketInput = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream socketOutput = new ObjectOutputStream(socket.getOutputStream());

                ) {

            String request = null;

            try(
                    BufferedReader reader = new BufferedReader(new FileReader("request.json"))
                    ) {

                request = reader.lines().toString();

            }

            socketOutput.writeObject(request);
            socketOutput.flush();
            var response = socketInput.readObject();

        }


    }
}
