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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An HttpServletRequestWrapper that record the request content and provide some helpers.
 * Use #getRequestContent to get the response content.
 */
public class MonitoringServletRequest extends HttpServletRequestWrapper {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringServletRequest.class);

    /**
     * Object reader to deserialize json messages.
     */
    private final ObjectReader mapReader = new ObjectMapper().reader(Map.class);

    /**
     * The wrapped servlet.
     */
    private final HttpServletRequest httpServletRequest;

    /**
     * Record the stream read from the servlet.
     */
    private RecordingServletInputStream recordingServletInputStream;

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
    private final Date initialTimestamp = MonitoringUtilities.getCurrentTimestamp();

    /**
     * Timestamp when message has been received.
     */
    private final String initialTimestampAsString = MonitoringUtilities.formatDateAsRfc339(initialTimestamp);

    public MonitoringServletRequest(HttpServletRequest request) {
        super(request);
        this.httpServletRequest = request;

        String correlationIdCandidate;

        correlationIdCandidate = httpServletRequest.getHeader("X-Correlation-id");
        if (correlationIdCandidate == null) {
            correlationIdCandidate = MonitoringUtilities.createCorrelationId();
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
    public Date getInitialTimestamp() {
        return initialTimestamp;
    }

    /**
     * The initial timestamp as String.
     */
    public String getInitialTimestampAsString() {
        return initialTimestampAsString;
    }

    public String getEndpoint() {
        return getMethod() + " " + getRequestURI();
    }

    @Override
    public String toString() {
        return "MonitoringServletRequest{" +
                "content=" + recordingServletInputStream.getContent() +
                ", correlationId='" + correlationId + '\'' +
                ", initialTimestamp='" + initialTimestamp + '\'' +
                '}';
    }


}
