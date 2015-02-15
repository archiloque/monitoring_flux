package com.octo.monitoring_flux.middleend.dto;

/**
 * Response from the endpoint 2
 */
public class EndPoint2Response {

    private final String status;

    public EndPoint2Response(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
