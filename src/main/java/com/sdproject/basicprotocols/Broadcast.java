/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdproject.basicprotocols;

import com.sdproject.message.Header;
import com.sdproject.message.Message;
import com.sdproject.utils.Utils;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author escudero
 */
public class Broadcast {

    private final List<Socket> sockets;
    private int numberMessage;

    public Broadcast(int port) {
        this.sockets = new ArrayList<Socket>();
        this.sockets.add(Utils.openServerConnexion(port));
    }

    public Broadcast(int port, List<String> destinations) {
        this.sockets = Utils.openMultipleClientConnexions(destinations, port);
        this.numberMessage = 0;
    }

    public void runSenderProcess(String content) {
        Header header;
        Message message;
        
        for (int i = 0; i < this.sockets.size(); i++) {
            Socket machine = this.sockets.get(i);

            header = new Header("data", machine.getInetAddress().getHostAddress(), this.numberMessage++);
            message = new Message(header, content);

            Utils.sendMessage(machine, message);
        }
    }

    public void runReceiverProcess() {
        Utils.getMessage(sockets.get(0));
    }
}
