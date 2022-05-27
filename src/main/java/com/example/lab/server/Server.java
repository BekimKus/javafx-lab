package com.example.lab.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {

    public static List <Socket> clients = new ArrayList<>();
    public static Set <SocketAddress> clientsAddress = new HashSet<>();
    public static List<ServerSend> serverSends = new ArrayList<>();
    public static List<ServerReceive> serverReceives = new ArrayList<>();
    public static int clientNum = 0;
    public static int clientControl = 0;
    public static boolean receivedDeveloper;
    public static boolean receivedManager;
    public static boolean sentDevelopers;
    public static boolean sentManagers;
    public static String stringObjectDeveloper;
    public static String stringObjectManager;


    public static void main(String[] args) throws IOException {
       ServerSocket serverSocket = new ServerSocket(8000);

       while (clients.size() < 2) {
           Socket clientSocket = serverSocket.accept();

           clients.add(clientSocket);
           clientsAddress.add(clientSocket.getRemoteSocketAddress());
           System.out.println("Подключено клиентов " + ++clientNum);
           clientControl++;

           ServerReceive serverReceive = new ServerReceive(clientSocket, clientNum);
           serverReceives.add(serverReceive);
           serverReceive.start();

           ServerSend serverSend = new ServerSend(clientSocket, clientNum);
           serverSends.add(serverSend);
           serverSend.start();
       }

        try {
            Thread.sleep(1000);
//            System.out.println(Server.clients.get(0).isConnected());
//            System.out.println(Server.clients.get(1).isConnected());
            clients.get(0).close();
            clients.get(1).close();

            if (serverSends.get(0).isAlive()) {
                serverSends.get(0).interrupt();
                serverReceives.get(0).interrupt();
            }
            if (serverSends.get(1).isAlive()) {
                serverSends.get(1).interrupt();
                serverReceives.get(1).interrupt();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
