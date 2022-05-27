package com.example.lab.client;

import com.example.lab.Developer;
import com.example.lab.Employee;
import com.example.lab.Habitat;
import com.example.lab.Manager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class ClientSend extends Thread {

    public DataOutputStream writer;
    public Socket clientSocket;

    public ClientSend(Socket client) {
        clientSocket =client;
    }

    @Override
    public void run() {
        try {
            writer = new DataOutputStream(clientSocket.getOutputStream());

            Thread.sleep(100);

            if (ClientThread.clientControl == 1 && !ClientThread.sentDeveloper) {
                List<Employee> developers = Habitat.employees.stream()
                        .filter(employee -> employee instanceof Developer)
                        .collect(Collectors.toList());

                if (developers.isEmpty()) {
                    writer.writeUTF("No developers :(\n");
                } else {
                    writer.writeUTF(developers.toString());
                }
                ClientThread.sentDeveloper = true;
            }

            if (ClientThread.clientControl == 2 && !ClientThread.sentManager) {
                List<Employee> managers = Habitat.employees.stream()
                        .filter(employee -> employee instanceof Manager)
                        .collect(Collectors.toList());
                if (managers.isEmpty()) {
                    writer.writeUTF("No managers :(\n");
                } else {
                    writer.writeUTF(managers.toString());
                }

                ClientThread.sentManager = true;
            }

            while (true) {
                if (clientSocket.isClosed()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                System.out.println("ClientSend closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downService() {
        try {
            if(!clientSocket.isClosed()) {
                clientSocket.close();
                writer.close();
                interrupt();
                System.out.println("ClientSend downed");
            }
        } catch (IOException ignored) {}
    }
}
