package com.example.lab.client;

import java.io.*;
import java.net.Socket;

public class ClientReceive extends Thread {

    public DataInputStream reader;
    public Socket clientSocket;

    public ClientReceive(Socket client) {
        clientSocket = client;
    }

    @Override
    public void run() {
        try {
            reader = new DataInputStream(clientSocket.getInputStream());

            ClientThread.clientControl = reader.readInt();
//            System.out.println(ClientThread.clientControl);

            System.out.println(reader.readUTF());

            while (true) {
                if (ClientThread.receivedDeveloper
                    && ClientThread.receivedManager) {
                    break;
                }

                if (ClientThread.clientControl == 1 && !ClientThread.receivedManager) {
                    Thread.sleep(500);
                    ClientThread.stringObjectManager = reader.readUTF();
                    System.out.println(ClientThread.stringObjectManager);
                    ClientThread.receivedManager = true;

                    ClientThread.sentManager = reader.readBoolean();
                    break;
                }

                if (ClientThread.clientControl == 2 && !ClientThread.receivedDeveloper) {
                    Thread.sleep(500);
                    ClientThread.stringObjectDeveloper = reader.readUTF();
                    System.out.println(ClientThread.stringObjectDeveloper);
                    ClientThread.receivedDeveloper = true;

                    ClientThread.sentDeveloper = reader.readBoolean();
                    break;
                }


//                if (!string_check.equals(ClientThread.MESSAGE_HELLO))
//                {
//                    ClientThread.clientSends.get(0).flag=false;
//                    ClientThread.clientSends.remove(0);
//                    break;
//                }
//                ClientSend.string_check = ClientThread.MESSAGE_STOP;

            }
//            System.out.println("Клиент: Поток получения закончил работу");

        } catch (EOFException e) {
            System.out.println(ClientThread.object);
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                reader.close();
                System.out.println("ClientReceive closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void downService() {
        try {
            if(reader != null) {
                reader.close();
                interrupt();
                System.out.println("ClientReceive downed");
            }
        } catch (IOException ignored) {}
    }
}
