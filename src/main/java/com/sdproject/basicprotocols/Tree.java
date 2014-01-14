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
import java.util.List;

/**
 *
 * @author escudero
 */
public class Tree {

    private final List<Socket> socketsSender;
    private final Socket socketReceiver;
    private int numberMessage;

    public Tree(int port, List<String> receivers) {
        this.socketsSender = Utils.openMultipleClientConnexions(receivers, port);
        this.socketReceiver = null;
        this.numberMessage = 0;
    }

    public Tree(int portReceiver, int portSender, List<String> receivers) {
        this.socketsSender = Utils.openMultipleClientConnexions(receivers, portSender);
        this.socketReceiver = Utils.openServerConnexion(portReceiver);
    }

    public Tree(int portReceiver) {
        this.socketReceiver = Utils.openServerConnexion(portReceiver);
        this.socketsSender = null;
    }
    
    public void runRootProcess(String content) {
                Header header;
        Message message;
        
        for (int i = 0; i < this.socketsSender.size(); i++) {
            Socket machine = this.socketsSender.get(i);

            header = new Header("data", machine.getInetAddress().getHostAddress(), this.numberMessage++);
            message = new Message(header, content);

            Utils.sendMessage(machine, message);
        }
    }
    
    public void runNodeProcess() {
        Message message = (Message) Utils.getMessage(this.socketReceiver);
        
        Header header = message.getHeader();
                
        for (int i = 0; i < this.socketsSender.size(); i++) {
            Socket machine = this.socketsSender.get(i);
            header.setDestinationAddress(machine.getInetAddress().getHostAddress());

            Utils.sendMessage(machine, message);
        }
    }

    public void runLeafProcess() {
        Utils.getMessage(this.socketReceiver);
    }
}