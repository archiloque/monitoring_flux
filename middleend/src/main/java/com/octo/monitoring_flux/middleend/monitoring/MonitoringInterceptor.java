package com.octo.monitoring_flux.middleend.monitoring;

import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor adding monitoring capabilities.
 * Rely on a ResponseRecordingServletResponse to get the content response.
 */
public class MonitoringInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringInterceptor.class);

    private final MonitoringUtilities monitoringUtilities = new MonitoringUtilities();

    public MonitoringInterceptor(int zMQPort) {
        MonitoringUtilities.initialize(zMQPort);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request instanceof MonitoringServletRequest) {
            MonitoringServletRequest monitoringServletRequest = (MonitoringServletRequest) request;
            Map<String, Object> header = getHeaderFromRequest(monitoringServletRequest);
            String receivedServiceCallTimestamp = monitoringServletRequest.getInitialTimestamp();
            header.put("middleend_starting_service_call_timestamp", receivedServiceCallTimestamp);
            sendMonitoringMessage(
                    "starting_service_call",
                    receivedServiceCallTimestamp,
                    monitoringUtilities.createMonitoringMessage(
                            header,
                            monitoringServletRequest.getRequestContent(),
                            null,
                            monitoringServletRequest.getAllHeaders(),
                            null)
            );
        } else {
            LOGGER.info(request + " is not a MonitoringServletRequest");
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if ((request instanceof MonitoringServletRequest) && (response instanceof RecordingServletResponse)) {
            MonitoringServletRequest monitoringServletRequest = (MonitoringServletRequest) request;
            RecordingServletResponse recordingServletResponse = (RecordingServletResponse) response;

            String finalTimestamp = monitoringUtilities.getTimeStampAsRfc339();

            Map<String, Object> header = getHeaderFromRequest(monitoringServletRequest);
            String receivedServiceCallTimestamp = monitoringServletRequest.getInitialTimestamp();
            header.put("middleend_starting_service_call_timestamp", receivedServiceCallTimestamp);
            header.put("middleend_end_service_call_timestamp", finalTimestamp);
            sendMonitoringMessage(
                    "ending_service_call",
                    finalTimestamp,
                    monitoringUtilities.createMonitoringMessage(
                            header,
                            monitoringServletRequest.getRequestContent(),
                            recordingServletResponse.getResponseContent(),
                            monitoringServletRequest.getAllHeaders(),
                            null)
            );
        } else {
            if (!(request instanceof MonitoringServletRequest)) {
                LOGGER.info(request + " is not a MonitoringServletRequest");
            }
            if (!(response instanceof RecordingServletResponse)) {
                LOGGER.info(response + " is not a RecordingServletResponse");
            }
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    private Map<String, Object> getHeaderFromRequest(MonitoringServletRequest monitoringServletRequest) {
        Map<String, Object> header = new HashMap<>();
        header.put("correlation_id", monitoringServletRequest.getCorrelationId());
        header.put("path", monitoringServletRequest.getRequestURI());
        return header;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    /**
     * Send a message to the monitoring system.
     *
     * @param messageType the message type
     * @param timestamp   the message timestamp (may be null)
     * @param content     the base message content
     */
    private void sendMonitoringMessage(String messageType, String timestamp, Map<String, Object> content) {
        monitoringUtilities.sendMonitoringMessage(getClass(), messageType, timestamp, content);
    }
}
