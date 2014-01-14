/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sdproject.basicprotocols;

import com.sdproject.message.Header;
import com.sdproject.message.Message;
import com.sdproject.utils.Utils;
import java.net.Socket;

/**
 *
 * @author oli
 */
public class Unicast {
    
    private final Socket socket;
    private int numberMessage;

    public Unicast(int port) {
        this.socket = Utils.openServerConnexion(port);
    }
    
    public Unicast(int port, String receiver) {
        this.socket = Utils.openClientConnexion(receiver, port);
        this.numberMessage = 0;
    }

    public void runSenderProcess(String content) {
        Header header = new Header("data", this.socket.getInetAddress().getHostAddress(), this.numberMessage++);
        Message message = new Message(header, content);
        
        Utils.sendMessage(socket, message);
    }

    public void runReceiverProcess() {
        Utils.getMessage(this.socket);
    }
}
