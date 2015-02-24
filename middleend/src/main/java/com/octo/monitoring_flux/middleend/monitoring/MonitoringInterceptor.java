package com.octo.monitoring_flux.middleend.monitoring;

import com.octo.monitoring_flux.shared.MonitoringMessenger;
import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor adding monitoring capabilities.
 * Rely on a ResponseRecordingServletResponse to get the content response.
 */
public class MonitoringInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringInterceptor.class);

    private final MonitoringMessenger monitoringMessenger;

    public MonitoringInterceptor(String moduleType, String moduleId, int zMQPort) {
        monitoringMessenger = new MonitoringMessenger(moduleType, moduleId, zMQPort);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request instanceof MonitoringServletRequest) {
            MonitoringServletRequest monitoringServletRequest = (MonitoringServletRequest) request;
            monitoringMessenger.sendMonitoringMessage(
                    monitoringServletRequest.getCorrelationId(),
                    monitoringServletRequest.getEndpoint(),
                    "Begin call",
                    monitoringServletRequest.getInitialTimestampAsString(),
                    monitoringServletRequest.getInitialTimestampAsString(),
                    null,
                    null,
                    monitoringServletRequest.getRequestContent(),
                    monitoringServletRequest.getAllHeaders(),
                    null,
                    null);
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

            Date finalTimestamp = MonitoringUtilities.getCurrentTimestamp();
            String finalTimestampAsString = MonitoringUtilities.formatDateAsRfc339(finalTimestamp);

            Map<String, Object> responseForMonitoring = new HashMap<>();
            responseForMonitoring.put("code", response.getStatus());
            responseForMonitoring.put("content", recordingServletResponse.getResponseContent());

            monitoringMessenger.sendMonitoringMessage(
                    monitoringServletRequest.getCorrelationId(),
                    monitoringServletRequest.getEndpoint(),
                    "End call",
                    finalTimestampAsString,
                    monitoringServletRequest.getInitialTimestampAsString(),
                    finalTimestampAsString,
                    ((double) (finalTimestamp.getTime() - monitoringServletRequest.getInitialTimestamp().getTime())) / 1000,
                    monitoringServletRequest.getRequestContent(),
                    monitoringServletRequest.getAllHeaders(),
                    responseForMonitoring,
                    null);


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

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
