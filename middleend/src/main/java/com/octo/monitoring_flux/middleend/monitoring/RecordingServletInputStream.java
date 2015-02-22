package com.octo.monitoring_flux.middleend.monitoring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * A ServletInputStream that record the request as it's being read.
 * Use #getContent to get the content.
 */
class RecordingServletInputStream extends ServletInputStream {

    private final ByteArrayOutputStream byteArrayOutputStream;

    /**
     * InputStream.
     */
    private final ServletInputStream servletInputStream;

    /**
     * @param httpServletRequest
     * @throws IOException
     */
    public RecordingServletInputStream(HttpServletRequest httpServletRequest) throws IOException {
        this.servletInputStream = httpServletRequest.getInputStream();
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        int value = servletInputStream.read();
        byteArrayOutputStream.write(value);
        return value;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        servletInputStream.close();
    }

    /**
     * @return
     */
    public String getContent() {
        return byteArrayOutputStream.toString();
    }

}