package com.octo.monitoring_flux.middleend.dto;

/**
 * Request for the endpoint 2.
 */
public class EndPoint2Request {

    private int numberOfMessages;

    private int timeToSpend;

    public EndPoint2Request() {
    }

    public int getTimeToSpend() {
        return timeToSpend;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setTimeToSpend(int timeToSpend) {
        this.timeToSpend = timeToSpend;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }
}
