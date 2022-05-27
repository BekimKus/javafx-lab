package com.example.lab.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread{
    public Socket clientSocket;
    public DataInputStream reader;
    public DataOutputStream writer;

    protected int num;
    protected boolean flag;
}
