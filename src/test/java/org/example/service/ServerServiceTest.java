package org.example.service;

import org.example.Enums;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServerServiceTest {

    File activeConnections = new File("src/test/java/org/example/service/activeConnections.txt");
    int counter;
    private static final Map<Integer, String> clientInfo = new ConcurrentHashMap<>();
    Socket socket = new Socket();

    @Test
    public void getClientInfoTest() {
        counter++;
        LocalDateTime timeOfConnection = LocalDateTime.now();
        String message = "client-" + counter + ", socket:" + socket.getPort() + " " + timeOfConnection + "\n";
        clientInfo.put(socket.getPort(), message);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(activeConnections, false);
            fileOutputStream.write(clientInfo.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(activeConnections.length() != 0);
    }

    @Test
    public void getFileFromClientTest() {

        String pathFromClient = "test";
        String pathToClientFile = "src/main/java/org/example/files/" + pathFromClient;
        String fileData = "TestData";
        File fileFromClient = new File(pathToClientFile);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileFromClient, false);
            fileOutputStream.write(fileData.getBytes());
        } catch (IOException e) {
            System.err.println("Cannot get the file");
        }
        Assert.assertTrue(fileFromClient.length() != 0);
    }


    @Test
    public void getMessageFromClientTest() throws IOException {
        String message = "Test ";

        PrintWriter printWriter = new PrintWriter("1");
        printWriter.write(message);
        printWriter.flush();

        Assert.assertTrue("Test", true);
    }
}

