/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdproject.main;

import java.util.List;
import com.sdproject.basicprotocols.Broadcast;
import com.sdproject.basicprotocols.Pipeline;
import com.sdproject.basicprotocols.Tree;
import com.sdproject.basicprotocols.Unicast;
import com.sdproject.utils.Utils;
import complexprotocols.UniformDiffusion;

/**
 *
 * @author escudero
 */
public class Execute {

    private static void runUnicast(String typeMachine, String[] parameters) {
        Unicast unicast;
        int port = Integer.parseInt(parameters[2]);

        if (typeMachine.equals("-sender")) {
            unicast = new Unicast(port, parameters[3]);
            unicast.runSenderProcess("This is a new message");
        } else if (typeMachine.equals("-receiver")) {
            unicast = new Unicast(port);
            unicast.runReceiverProcess();
        }
    }

    private static void runBroadcast(String typeMachine, String[] parameters) {
        Broadcast broadcast;
        int port = Integer.parseInt(parameters[2]);

        if (typeMachine.equals("-sender")) {
            int index = 3;
            List<String> destinations = Utils.getAddressesFromConsole(parameters, index);
            broadcast = new Broadcast(port, destinations);
            broadcast.runSenderProcess("This is a new message");

        } else if (typeMachine.equals("-receiver")) {
            broadcast = new Broadcast(port);
            broadcast.runReceiverProcess();
        }
    }

    private static void runPipeline(String typeMachine, String[] parameters) {
        Pipeline pipeline;
        int port = Integer.parseInt(parameters[2]);
        String destination;

        if (typeMachine.equals("-sender")) {
            destination = parameters[3];
            pipeline = new Pipeline(port, destination);
            pipeline.runSenderProcess("This is a new message");

        } else if (typeMachine.equals("-node")) {
            int portInput = port;
            int portOutput = Integer.parseInt(parameters[3]);
            destination = parameters[4];

            pipeline = new Pipeline(portInput, portOutput, destination);
            pipeline.runNodeProcess();
            
        } else if (typeMachine.equals("-receiver")) {
                pipeline = new Pipeline(port);
                pipeline.runReceiverProcess();
        }
    }

    private static void runTree(String typeMachine, String[] parameters) {
        Tree tree;
        int port = Integer.parseInt(parameters[2]);
        List<String> destinations;
        int index;

        if (typeMachine.equals("-root")) {
            index = 3;
            destinations = Utils.getAddressesFromConsole(parameters, index);
            tree = new Tree(port, destinations);
            tree.runRootProcess("This is a new message");

        } else if (typeMachine.equals("-node")) {
            index = 4;
            int portInput = port;
            int portOutput = Integer.parseInt(parameters[3]);
            destinations = Utils.getAddressesFromConsole(parameters, index);

            tree = new Tree(portInput, portOutput, destinations);
            tree.runNodeProcess();

        } else if (typeMachine.equals("-leaf")) {
            tree = new Tree(port);
            tree.runLeafProcess();
        }
    }

    private static void runUniform(String typeMachine, String[] parameters) {
        UniformDiffusion uniform;
        int port = Integer.parseInt(parameters[2]);
        List<String> destinations;
        int index;

        if (typeMachine.equals("-sender")) {
            index = 5;
            int portInput = port;
            int portOutput = Integer.parseInt(parameters[3]);
            String sequencer = parameters[4];
            destinations = Utils.getAddressesFromConsole(parameters, index);
            
            uniform = new UniformDiffusion(portInput, portOutput, destinations, sequencer);
            uniform.runSenderProcess("This is a new message");

        } else if (typeMachine.equals("-sequencer")) {
            int portInput = port;
            int portOutput = Integer.parseInt(parameters[3]);

            uniform = new UniformDiffusion("sequencer", portInput, portOutput);
            uniform.runSequencerProcess();

        } else if (typeMachine.equals("-receiver")) {
            int portInput = port;
            int portOutput = Integer.parseInt(parameters[3]);
            uniform = new UniformDiffusion("receiver", portInput, portOutput);
            uniform.runReceiverProcess();
        }
    }

    private static void errorWithParameters() {
        System.out.println("Help: ./sd_proj -[protocol] -[type] -[other_parameters]");
        System.out.println("Protocols: unicast | broadcast | pipeline | tree | uniform");
        System.out.println("Type: sender | receiver | ...");
        System.out.println("Other parameters: depending on the protocol (read README)");
    }

    public static void main(String[] args) {

        if (args.length < 3) {
            errorWithParameters();
            System.exit(1);
        }

        String protocol = args[0];
        String typeMachine = args[1];

        String[] parameters = args;

        if (protocol.equals("-unicast")) {
                runUnicast(typeMachine, parameters);
                
        } else if (protocol.equals("-broadcast")) {
                runBroadcast(typeMachine, parameters);
                
        } else if (protocol.equals("-pipeline")) {
            runPipeline(typeMachine, parameters);
            
        } else if (protocol.equals("-tree")) {
            runTree(typeMachine, parameters);

        } else if (protocol.equals("-uniform")) {
            runUniform(typeMachine, parameters);
        } else {
            errorWithParameters();
        }
    }
}
