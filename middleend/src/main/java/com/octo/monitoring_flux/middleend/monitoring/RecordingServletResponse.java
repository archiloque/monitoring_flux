package com.octo.monitoring_flux.middleend.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * An HttpServletResponseWrapper that record the response.
 * Use #getResponseContent to get the response content.
 */
public class RecordingServletResponse extends HttpServletResponseWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingServletResponse.class);

    private final ObjectReader mapReader = new ObjectMapper().reader(Map.class);

    private final HttpServletResponse response;

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

        private final ServletOutputStream servletOutputStream;

        private final ByteArrayOutputStream byteArrayOutputStream;

        public RecordingServletOutputStream(ServletResponse httpServletResponse) throws IOException {
            this.servletOutputStream = httpServletResponse.getOutputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
        }

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
