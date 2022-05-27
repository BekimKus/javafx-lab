package com.example.lab.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSend extends ServerThread {

    public ServerSend(Socket client,int number) {
        clientSocket = client;
        num = number;
    }

    @Override
    public void run() {
        try {
            writer = new DataOutputStream(clientSocket.getOutputStream());
            int currentConnections = Server.clients.size();

            writer.writeInt(currentConnections);
            writer.writeUTF(Server.clientsAddress.toString());

            while (true) {

                if (Server.serverSends.size() == 2) {
                    Server.serverSends.get(1).sendDevelopers();
                    Server.serverSends.get(0).sendManagers();
//                    Thread.sleep(1000);
//                    break;
                }

                if (Server.sentManagers
                    && Server.sentDevelopers) {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                System.out.println("ServerSend closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendManagers() throws IOException {
        if (Server.receivedManager
                && !Server.sentManagers) {
            writer.writeUTF(Server.stringObjectManager);
            writer.flush();
            System.out.println("Server sent manager: " + Server.stringObjectManager);
            Server.sentManagers = true;

            writer.writeBoolean(Server.receivedManager);
        }
    }

    private void sendDevelopers() throws IOException {
        if (Server.receivedDeveloper
                && !Server.sentDevelopers) {
            writer.writeUTF(Server.stringObjectDeveloper);
            writer.flush();
            System.out.println("Server sent dev: " + Server.stringObjectDeveloper);
            Server.sentDevelopers = true;

            writer.writeBoolean(Server.receivedDeveloper);
        }
    }

    private void downService() {
        try {
            if(!clientSocket.isClosed()) {
                clientSocket.close();
                writer.close();
//                reader.close();
                interrupt();
                System.out.println("ServerSend downed");
            }

        } catch (IOException ignored) {}
    }
}