/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdproject.utils;

import com.sdproject.message.Header;
import com.sdproject.message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author escudero
 */
public class Utils {

    public static List<String> getAddressesFromConsole(String[] parameters, int index) {
        List<String> addresses = new ArrayList<String>();

        for (int i = index; i < parameters.length; i++) {
            addresses.add(parameters[i]);
        }

        return addresses;
    }

    public static Socket openServerConnexion(int port) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        Socket socket = null;

        if (server != null) {
            try {
                socket = server.accept();
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return socket;
    }

    public static Socket openClientConnexion(String serverAddress, int port) {
        Socket socket = null;

        try {
            socket = new Socket(serverAddress, port);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return socket;
    }

    public static List<Socket> openMultipleClientConnexions(List<String> destinations, int port) {
        Socket socket;
        List<Socket> sockets = new ArrayList<Socket>();

        for (int i = 0; i < destinations.size(); i++) {
            socket = openClientConnexion(destinations.get(i), port);
            sockets.add(socket);
        }

        return sockets;
    }

    public static void closeConnexion(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendMessage(Socket socket, Message message) {
        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(message);
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("-- MESSAGE SENT --");
        System.out.println(message.toString());
        System.out.println("------------------");
    }

    public static Message getMessage(Socket socket) {
        ObjectInputStream input = null;
        Message message = null;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            message = (Message) input.readObject();
            input.close();
            System.out.println("-- MESSAGE RECEIVED --");
            System.out.println(message.toString());
            System.out.println("----------------------");
            return message;
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return message;
    }

    public static double calculateLatency(Socket socket, String typeMachine)
            throws IOException, ClassNotFoundException {
        long latency = 0;
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Message messageClient;
        Message messageServer;

        /*switch (typeMachine) {
         case "server":
         messageClient = Utils.getMessage(input);
         //System.out.println(messageClient.toString());
         messageServer = new Message("Latence".getBytes(), new Date());
         Utils.sendMessage(output, messageServer);
         break;
         case "client":
         messageClient = new Message("Message for the latency".getBytes(), new Date());
         sendMessage(output, messageClient);
         messageServer = getMessage(input);
         latency = (new Date()).getTime()
         - messageServer.getSendingDate().getTime();
         break;
         }*/
        return latency;

    }

    public static double calculateThroughput(Socket socket, String typeMachine)
            throws IOException, ClassNotFoundException {
        double throughput = 0;
        ObjectOutputStream output;
        ObjectInputStream input;

        /*switch (typeMachine) {
         case "client":
         byte[] messageUtil = new byte[8];
         Date timeBeforeSending = new Date();
         for (int i = 0; i < 100; i++) {
         output = new ObjectOutputStream(socket.getOutputStream());
         Message message = new Message(messageUtil, new Date());
         sendMessage(output, message);
         }
         Date timeAfterSending = new Date();
         double timeLap = (timeAfterSending.getTime() - timeBeforeSending.getTime());
         throughput = ((100 * 8) / timeLap);
         break;
         case "server":
         for (int i = 0; i < 100; i++) {
         input = new ObjectInputStream(socket.getInputStream());
         Message messageClientT = Utils.getMessage(input);
         }
         break;
         }*/
        return throughput;
    }

    public static List<String> convertStringToList(String string) {
        String newString = string.replace("[", "").replace("]", "");
        List<String> listOfStrings = new ArrayList<String>(Arrays.asList(newString.split(",")));

        return listOfStrings;
    }

    public static void deliverMessage(Message message) {
        System.out.println("--------------- DELIVERED ---------------");
        System.out.println(message);
        System.out.println("-----------------------------------------");
    }
}
