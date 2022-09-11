package org.example.engine;

import org.example.engine.processor.ClassPathDIEngine;

public class TcpApplication {

    public static void main(String[] args) throws InterruptedException {
        ClassPathDIEngine engine = new ClassPathDIEngine();


        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                engine.start();
            }
        });

        newThread.join();
        newThread.start();







    }

}
