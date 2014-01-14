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

/**
 *
 * @author escudero
 */
public class Pipeline {

    private final Socket socketSender;
    private final Socket socketReceiver;
    private int numberMessage;

    public Pipeline(int port, String receiver) {
        this.socketSender = Utils.openClientConnexion(receiver, port);
        this.socketReceiver = null;
        this.numberMessage = 0;
    }

    public Pipeline(int portReceiver, int portSender, String receiver) {
        this.socketSender = Utils.openClientConnexion(receiver, portSender);
        this.socketReceiver = Utils.openServerConnexion(portReceiver);
    }

    public Pipeline(int port) {
        this.socketReceiver = Utils.openServerConnexion(port);
        this.socketSender = null;
    }

    public void runSenderProcess(String content) {
        Header header = new Header("data", this.socketSender.getInetAddress()
                .getHostAddress(), this.numberMessage++);
        Message message = new Message(header, content);
        
        Utils.sendMessage(this.socketSender, message);
    }

    public void runNodeProcess() {
        Message message = (Message) Utils.getMessage(this.socketReceiver);
        
        Header header = message.getHeader();
        header.setDestinationAddress(this.socketSender.getInetAddress().getHostAddress());
        
        Utils.sendMessage(this.socketSender, message);
    }

    public void runReceiverProcess() {
        Utils.getMessage(this.socketReceiver);
    }
}
