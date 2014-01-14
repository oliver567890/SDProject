/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package complexprotocols;

import com.sdproject.message.Header;
import com.sdproject.message.Message;
import com.sdproject.utils.Utils;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author escudero
 */
public class UniformDiffusion {

    private final Socket sequencer;
    private final Socket sender;
    private final List<Socket> receivers;

    // Sequence number. Modified only by the Sequencer.
    private int sequenceNumber;

    // Table of sequence numbers. Modified only by the Sequencer.
    private Map<String, List<Integer>> sequenceTable;

    // Messages waiting to be delivered. Modified only by the Receivers.
    private Map<Integer, Message> notDeliveredMessages;

    // Constructor for Sender.
    public UniformDiffusion(int portForReceivers, int portForSequencer,
            List<String> receivers, String sequencer) {
        this.receivers = Utils.openMultipleClientConnexions(receivers, portForReceivers);
        this.sequencer = Utils.openClientConnexion(sequencer, portForSequencer);

        Header header = new Header("sequencer_connexion_request",
                this.sequencer.getInetAddress().getHostAddress(), 0);
        Message message = new Message(header, receivers.toString());
        Utils.sendMessage(this.sequencer, message);

        this.sender = null;
    }

    // Constructor for Receivers.
    public UniformDiffusion(int portReceiver) {
        this.sender = Utils.openServerConnexion(portReceiver);
        this.sequencer = Utils.openServerConnexion(portReceiver++);
        this.receivers = null;

        // Initialize not delivered message's table.
        this.notDeliveredMessages = new HashMap<Integer, Message>();
    }

    // Constructor for Sequencer.
    public UniformDiffusion(int portSequencer, int portForReceivers) {
        this.sender = Utils.openServerConnexion(portSequencer);

        Message message = Utils.getMessage(this.sender);
        String stringAddresses = message.getInformation();
        List<String> addresses = Utils.convertStringToList(stringAddresses);

        this.receivers = Utils.openMultipleClientConnexions(addresses, portForReceivers);
        this.sequencer = null;

        // Initialize sequence table.
        this.sequenceTable = new HashMap<String, List<Integer>>();

        for (String address : addresses) {
            this.sequenceTable.put(address, new ArrayList<Integer>());
        }

        // Initialize sequence number.
        this.sequenceNumber = 1;
    }

    public void runSenderProcess(String content) {
        // We send to the sequencer the number of machines that are going
        // to receive the message.
        Header header = new Header("get_sequence_number", this.sequencer
                .getInetAddress().getHostAddress(), 0);
        Message message = new Message(header, "");
        Utils.sendMessage(this.sequencer, message);

        // The sequencer answers us, giving the sequence number.
        message = Utils.getMessage(this.sequencer);
        int seqNumber = Integer.parseInt(message.getInformation());

        for (int i = 0; i < this.receivers.size(); i++) {
            Socket machine = this.receivers.get(i);

            header = new Header("data", machine.getInetAddress().getHostAddress(), seqNumber);
            message = new Message(header, content);

            Utils.sendMessage(machine, message);
        }
    }

    public void runSequencerProcess() {
        Runnable taskForSender = new SequencerProcess(this.sender, this.sequenceNumber,
                this.sequenceTable, null);
        Thread workerForSender = new Thread(taskForSender);
        workerForSender.start();
        
        Runnable taskForReceiver;
        Thread workerForReceiver;

        for (Socket receiver : this.receivers) {
            taskForReceiver = new SequencerProcess(receiver, this.sequenceNumber,
                    this.sequenceTable, this.receivers);
            workerForReceiver = new Thread(taskForReceiver);
            workerForReceiver.start();
        }
    }

    public void runReceiverProcess() {
        Runnable taskForSender = new ReceiverProcess(this.sender, this.notDeliveredMessages, this.sequencer);
        Runnable taskForSequencer = new ReceiverProcess(this.sequencer, this.notDeliveredMessages, null);

        Thread workerForSender = new Thread(taskForSender);
        Thread workerForSequencer = new Thread(taskForSequencer);

        // Start the threads.
        workerForSender.start();
        workerForSequencer.start();
    }
}
