package com.octo.monitoring_flux.middleend.dto;

/**
 * Response from the endpoint 1
 */
public class EndPoint1Response {

    private final String status;

    public EndPoint1Response(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
