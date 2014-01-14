/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sdproject.message;

/**
 *
 * @author oli
 */
public class Header {
    
    private String type;
    private String destinationAddress;
    private int sequenceNumber;

    public Header() {
        
    }

    public Header(String type, String destinationAddress, int sequenceNumber) {
        this.type = type;
        this.destinationAddress = destinationAddress;
        this.sequenceNumber = sequenceNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
    
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
