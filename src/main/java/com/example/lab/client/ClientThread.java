package com.example.lab.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {

    public static List<ClientSend> clientSends = new ArrayList<ClientSend>();
    public static List<ClientReceive> clientReceives = new ArrayList<ClientReceive>();

    public static boolean sentDeveloper;
    public static boolean sentManager;
    public static boolean receivedManager;
    public static boolean receivedDeveloper;

    public static boolean socketRunning = true;
    public static int clientControl = 0;
    public static String stringObjectDeveloper;
    public static String stringObjectManager;
    public static Object object;

    @Override
    public void run() {
        try {
            serverStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void serverStart() throws IOException {
        Socket clientSocket = new Socket("localhost",8000);
        ClientSend clientSend = new ClientSend(clientSocket);
        clientSends.add(clientSend);
        clientSend.start();

        ClientReceive clientReceive = new ClientReceive(clientSocket);
        clientReceives.add(clientReceive);
        clientReceive.start();
    }
}
