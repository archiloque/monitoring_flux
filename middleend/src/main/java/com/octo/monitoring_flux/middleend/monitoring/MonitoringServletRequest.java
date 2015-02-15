package com.octo.monitoring_flux.middleend.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An HttpServletRequestWrapper that record the request content and provide some helpers.
 * Use #getRequestContent to get the response content.
 */
public class MonitoringServletRequest extends HttpServletRequestWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringServletRequest.class);

    private final ObjectReader mapReader = new ObjectMapper().reader(Map.class);

    private final HttpServletRequest httpServletRequest;

    private RecordingServletInputStream recordingServletInputStream;

    private final static MonitoringUtilities MONITORING_UTILITIES = new MonitoringUtilities();

    /**
     * Correlation id;
     */
    private final String correlationId;

    /**
     * All all headers;
     */
    private final Map<String, String> allHeaders = new HashMap<>();

    /**
     * Timestamp when message has been received.
     */
    private final String initialTimestamp = MONITORING_UTILITIES.getTimeStampAsRfc339();

    public MonitoringServletRequest(HttpServletRequest request) {
        super(request);
        this.httpServletRequest = request;

        String correlationIdCandidate;

        correlationIdCandidate = httpServletRequest.getHeader("correlation)id");
        if (correlationIdCandidate == null) {
            correlationIdCandidate = MONITORING_UTILITIES.createCorrelationId();
        }
        correlationId = correlationIdCandidate;

        Enumeration<String> headerNames = getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            Enumeration<String> headers = getHeaders(headerName);
            List<String> values = new ArrayList<>();
            while (headers.hasMoreElements()) {
                values.add(headers.nextElement());
            }
            allHeaders.put(headerName, StringUtils.collectionToCommaDelimitedString(values));
        }
    }

    /**
     * Wrap a RecordingServletInputStream around the response
     */
    @Override
    public ServletInputStream getInputStream() {
        if (recordingServletInputStream == null) {
            try {
                recordingServletInputStream = new RecordingServletInputStream(httpServletRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return recordingServletInputStream;

    }

    /**
     * Get the request content.
     */
    public Map<String, ?> getRequestContent() {
        if (recordingServletInputStream != null) {
            try {
                return mapReader.readValue(recordingServletInputStream.getContent());
            } catch (IOException e) {
                LOGGER.info("Can't deserialize [" + recordingServletInputStream.getContent() + "]");
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * All the request headers..
     */
    public Map<String, String> getAllHeaders() {
        return allHeaders;
    }

    /**
     * Return the correlation id.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * The initial timestamp.
     */
    public String getInitialTimestamp() {
        return initialTimestamp;
    }

    @Override
    public String toString() {
        return "MonitoringServletRequest{" +
                "content=" + recordingServletInputStream.getContent() +
                ", correlationId='" + correlationId + '\'' +
                ", initialTimestamp='" + initialTimestamp + '\'' +
                '}';
    }

    /**
     * A ServletInputStream that record the request as it's being read.
     * Use #getContent to get the content.
     */
    private static final class RecordingServletInputStream extends ServletInputStream {

        private final ByteArrayOutputStream byteArrayOutputStream;

        private final ServletInputStream servletInputStream;

        private RecordingServletInputStream(HttpServletRequest httpServletRequest) throws IOException {
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

        public String getContent() {
            return byteArrayOutputStream.toString();
        }

    }


}
