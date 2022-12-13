package org.example.service;

import org.example.Enums;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerService {
    private static int counter;
    private static final Map<Integer, String> clientInfo = new ConcurrentHashMap<>();
    private static final ArrayList<Socket> clientSockets = new ArrayList<>();
    private static final String pathToFile = "src/main/java/org/example/files/activeConnections.txt";
    private static final File activeConnections = new File(pathToFile);

    private void writeFileToBytes(File file, String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.write(str.getBytes());
        } catch (IOException e) {
            System.err.println("Cannot get the file");
        }
    }

    private void getFileFromClient(Socket socket) {
        messageSender(socket, Enums.pathToFile.toString());

        String pathFromClient = messageReader(socket);
        String pathToClientFile = "src/main/java/org/example/files/" + pathFromClient;

        String file = messageReader(socket);
        File fileFromClient = new File(pathToClientFile);
        writeFileToBytes(fileFromClient, file);
    }


    private void writeDataToMap(File file, Map<Integer, String> clientInfo) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.write(clientInfo.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String messageReader(Socket socket) {
        String message = "";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            message = bufferedReader.readLine();
            if (message.equals("exit")) socket.close();

        } catch (IOException e) {
            System.err.println("[messageReader]" + Enums.clientDisconnected + socket.getPort());
        }
        return message;
    }

    private void messageSender(Socket socket, String message) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.write(message + "\n");
            printWriter.flush();

        } catch (IOException e) {
            System.err.println("[messageSender]" + Enums.clientDisconnected.toString() + socket.getPort());
        }
    }


    private void getMessageFromClient(Socket socket) {
        messageSender(socket, Enums.clientMessage.toString());
        String message = "";

        while (!message.equals("exit")) {
            message = messageReader(socket);
            System.out.println("Client " + socket.getPort() + ": " + message);
        }
    }


    public void getClientInfo(Socket socket) {
        counter++;
        LocalDateTime timeOfConnection = LocalDateTime.now();
        String message = "client-" + counter + ", socket:" + socket.getPort() + " " + timeOfConnection + "\n";
        clientInfo.put(socket.getPort(), message);
        writeDataToMap(activeConnections, clientInfo);
    }


    private void deleteInactiveClient(Socket socket) {
        for (Integer socket1 : clientInfo.keySet()) {
            if (socket1 == socket.getPort()) {
                clientInfo.remove(socket1);
                writeDataToMap(activeConnections, clientInfo);
            }
        }
    }


    public void clientsConnectionNotification(Socket socket) {
        String message = Enums.connectionNotification.toString() + counter;
        clientSockets.add(socket);
        for (Socket clientSocket : clientSockets) {
            messageSender(clientSocket, message);
        }
    }


    public void exitOrContinue(Socket socket) {
        new Thread(() -> {
            while (!(socket.isClosed())) {
                messageSender(socket, Enums.menuMessage.toString());
                switch (messageReader(socket)) {
                    case "1" -> getFileFromClient(socket);
                    case "2" -> getMessageFromClient(socket);
                    case "exit" -> {
                        try {
                            socket.close();
                            deleteInactiveClient(socket);
                        } catch (IOException e) {
                            System.err.println(Enums.clientDisconnected.toString() + socket.getPort());
                        }
                    }
                }
            }
        }).start();
    }
}
