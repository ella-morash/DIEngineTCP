package org.example.engine;

public class TcpApplication {

    public static void main(String[] args) {
        ClassPathDIEngine engine = new ClassPathDIEngine();
        engine.start();
    }
}
