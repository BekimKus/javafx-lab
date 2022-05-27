package com.example.lab.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ServerReceive extends ServerThread {

    public ServerReceive(Socket client, int number) {
       clientSocket = client;
       num = number;
   }
    @Override
    synchronized public void run() {
        try {
            reader = new DataInputStream(clientSocket.getInputStream());

            if (!Server.receivedDeveloper) {
                Server.stringObjectDeveloper = reader.readUTF();
                System.out.println("Server receive: " + Server.stringObjectDeveloper);
                Server.receivedDeveloper = true;
            }

            while (true) {
                if (Server.receivedManager
                        && Server.receivedDeveloper) {
                    break;
                }

                if (!Server.receivedManager && Server.clientControl == 2) {
                    Server.stringObjectManager = reader.readUTF();
                    System.out.println("Server receive: " + Server.stringObjectManager);
                    Server.receivedManager = true;
                    break;
                }
            }
        }
        catch (EOFException ignored) {
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                System.out.println("ServerReceive closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downService() {
        try {
            if(reader != null) {
                reader.close();
                interrupt();
                System.out.println("ServerReceive downed");
            }

        } catch (IOException ignored) {}
    }
}
