package com.octo.monitoring_flux.middleend.monitoring;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * An HttpServletResponseWrapper that record the response.
 * Use #getResponseContent to get the response content.
 */
public class RecordingServletResponse extends HttpServletResponseWrapper {

    /**
     * The wrapped HttpServletResponse.
     */
    private final HttpServletResponse response;

    /**
     * A stream that will record the response being written.
     */
    private RecordingServletOutputStream recordingServletOutputStream;

    public RecordingServletResponse(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    /**
     * Wrap a RecordingServletOutputStream around the response
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (recordingServletOutputStream == null) {
            recordingServletOutputStream = new RecordingServletOutputStream(response);
        }
        return recordingServletOutputStream;
    }

    /**
     * Get the response content.
     */
    public String getResponseContent() {
        if (recordingServletOutputStream != null) {
            return recordingServletOutputStream.getContent();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return getClass() + " {content=" + getResponseContent() + "}";
    }

    /**
     * A ServletOutputStream that record the response as it's being written.
     * Use #getContent to get the content.
     */
    private static final class RecordingServletOutputStream extends ServletOutputStream {

        /**
         * The wrapped RecordingServletOutputStream.
         */
        private final ServletOutputStream servletOutputStream;

        /**
         * Contain the recorded response.
         */
        private final ByteArrayOutputStream byteArrayOutputStream;

        public RecordingServletOutputStream(ServletResponse httpServletResponse) throws IOException {
            this.servletOutputStream = httpServletResponse.getOutputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
        }

        /**
         * Get the recorded content.
         *
         * @return a non-null String
         */

        public String getContent() {
            return byteArrayOutputStream.toString();
        }

        @Override
        public void write(int b) throws IOException {
            servletOutputStream.write(b);
            byteArrayOutputStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            servletOutputStream.flush();
        }

        @Override
        public void close() throws IOException {
            servletOutputStream.close();
        }

    }


}
