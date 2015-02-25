package com.octo.monitoring_flux.middleend.monitoring;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A ServletInputStream that record the request as it's being read.
 * Use #getContent to get the content.
 */
class RecordingServletInputStream extends ServletInputStream {

    /**
     * Contain the recorded request.
     */
    private final ByteArrayOutputStream byteArrayOutputStream;

    /**
     * The wrapped ServletInputStream.
     */
    private final ServletInputStream servletInputStream;

    public RecordingServletInputStream(HttpServletRequest httpServletRequest) throws IOException {
        this.servletInputStream = httpServletRequest.getInputStream();
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public int read() throws IOException {
        int value = servletInputStream.read();
        byteArrayOutputStream.write(value);
        return value;
    }

    @Override
    public void close() throws IOException {
        servletInputStream.close();
    }

    /**
     * Get the recorded content.
     * @return a non-null String
     */
    public String getContent() {
        return byteArrayOutputStream.toString();
    }

}