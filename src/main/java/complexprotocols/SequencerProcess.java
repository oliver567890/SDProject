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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author oli
 */
public class SequencerProcess implements Runnable {
    
    private final Socket socket;
    private int sequenceNumber;
    private final Map<String, List<Integer>> sequenceTable;
    private final List<Socket> receivers;

    public SequencerProcess(Socket socket, int sequenceNumber, 
            Map<String, List<Integer>> sequenceTable, List<Socket> receivers) {
        this.socket = socket;
        this.sequenceNumber = sequenceNumber;
        this.sequenceTable = sequenceTable;
        this.receivers = receivers;
    }

    public void run() {
        Message message;
        Header header;
        
        do {
            message = Utils.getMessage(socket);

            if (message.getHeader().getType().equals("get_sequence_number")) {
                // We take the message from the Sender.
                header = new Header("send_sequence_number", socket.getInetAddress().getHostAddress(), 0);
                message = new Message(header, String.valueOf(this.sequenceNumber));

                // We send a message with the sequence number, to the Sender.
                Utils.sendMessage(socket, message);
                this.sequenceNumber++;
            } else {
                // We take the message from the Receiver.
                int seqNumber = message.getHeader().getSequenceNumber();
                List<Integer> values = this.sequenceTable.get(socket.getInetAddress().getHostAddress());

                // We add the sequence number to its receiver list, in the sequence table.
                values.add(seqNumber);

                // If every receiver has this sequence number, we can validate
                // it and send a message to every receiver, confirming its delivery.
                boolean deliverMessage = true;
                Iterator<String> receiverHosts = this.sequenceTable.keySet().iterator();
                String host;

                while (receiverHosts.hasNext() && deliverMessage) {
                    host = receiverHosts.next();
                    values = this.sequenceTable.get(host);

                    if (!values.contains(seqNumber)) {
                        deliverMessage = false;
                    }
                }

                if (deliverMessage) {
                    for (int i = 0; i < this.receivers.size(); i++) {
                        Socket machine = this.receivers.get(i);
                        host = machine.getInetAddress().getHostAddress();

                        this.sequenceTable.get(host).remove(seqNumber);

                        header = new Header("deliver_message", host, seqNumber);
                        message = new Message(header, "");

                        Utils.sendMessage(machine, message);
                    }
                }
            }
        } while (true);
    }
}
