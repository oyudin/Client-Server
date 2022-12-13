package org.example;

import org.example.service.ServerService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

        ServerService serverService = new ServerService();

        try (ServerSocket servSocket = new ServerSocket(8093)) {

            while (true) {
                Socket socket = servSocket.accept();
                serverService.getClientInfo(socket);
                serverService.clientsConnectionNotification(socket);
                serverService.exitOrContinue(socket);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
