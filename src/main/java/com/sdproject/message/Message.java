/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdproject.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author escudero
 */
public class Message implements Externalizable {

    private Header header;
    private String information;

    public Message() {
    }

    public Message(Header header, String information) {
        this.header = header;
        this.information = information;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }  
    
    @Override
    public String toString() {
        StringBuilder messageString = new StringBuilder("-- Message -- \n");
        messageString.append("Type: ").append(this.header.getType()).append("\n");
        messageString.append("Content: ").append(this.information).append("\n");
        messageString.append("Destination Address: ").append(this.header.getDestinationAddress()).append("\n");
        messageString.append("Sequence Number: ").append(this.header.getSequenceNumber()).append("\n");
        
        return messageString.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.header.getType());
        out.writeObject(this.header.getDestinationAddress());
        out.writeObject(this.header.getSequenceNumber());
        out.writeObject(this.information);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.header = new Header();
        this.header.setType((String) in.readObject());
        this.header.setDestinationAddress((String) in.readObject());
        this.header.setSequenceNumber((Integer) in.readObject());
        this.information = (String) in.readObject();
    }
}
