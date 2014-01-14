/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package complexprotocols;

import com.sdproject.message.Header;
import com.sdproject.message.Message;
import com.sdproject.utils.Utils;
import java.net.Socket;
import java.util.Map;

/**
 *
 * @author oli
 */
public class ReceiverProcess implements Runnable {
    
    private final Socket socket;
    private final Map<Integer, Message> notDeliveredMessages;
    private final Socket sequencer;

    public ReceiverProcess(Socket socket, Map<Integer, Message> notDeliveredMessages, Socket sequencer) {
        this.socket = socket;
        this.notDeliveredMessages = notDeliveredMessages;
        this.sequencer = sequencer;
    }

    public void run() {
        Message message;
        Header header;

        do {
            message = Utils.getMessage(this.socket);

            if (message.getHeader().getType().equals("data")) {
                // We take the message from the Sender.
                this.notDeliveredMessages.put(message.getHeader()
                        .getSequenceNumber(), message);

                // We send the message to the Sequencer.
                header = new Header("message_received", this.sequencer
                        .getInetAddress().getHostAddress(),
                        message.getHeader().getSequenceNumber());
                message = new Message(header, "");
                Utils.sendMessage(this.sequencer, message);
            } else {
                // We take the validation message from the Sequencer.
                int seqNumber = message.getHeader().getSequenceNumber();

                message = this.notDeliveredMessages.remove(seqNumber);
                Utils.deliverMessage(message);
            }
        } while (true);
    }
}
