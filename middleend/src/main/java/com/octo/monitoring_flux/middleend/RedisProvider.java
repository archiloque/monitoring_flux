package com.octo.monitoring_flux.middleend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.monitoring_flux.middleend.monitoring.MonitoringServletRequest;
import com.octo.monitoring_flux.shared.MonitoringMessagesKeys;
import com.octo.monitoring_flux.shared.MonitoringMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import static com.octo.monitoring_flux.shared.MonitoringUtilities.getCurrentTimestampAsRfc339;

/**
 * Redis-related functions.
 */
@Component
public class RedisProvider {

    @Autowired
    private Environment environment;

    private Jedis jedis;

    private MonitoringMessenger monitoringMessenger;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisProvider.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void postConstruct() {
        jedis = new Jedis("localhost", Integer.parseInt(getEnvironmentValue("redis.port")));
        monitoringMessenger = new MonitoringMessenger(
                getEnvironmentValue("app.name"),
                getEnvironmentValue("app.name") + "." + ManagementFactory.getRuntimeMXBean().getName(),
                Integer.parseInt(getEnvironmentValue("zeromq.port"))
        );
        LOGGER.debug(jedis.ping());
    }

    public void postMessageToBackend(HttpServletRequest request, String key, Object messageBody) {
        MonitoringServletRequest monitoringServletRequest = (MonitoringServletRequest) request;

        String timestamp = getCurrentTimestampAsRfc339();

        Map<String, String> header = new HashMap<>();
        header.put(MonitoringMessagesKeys.MONITORING_MESSAGE_CORRELATION_ID, monitoringServletRequest.getCorrelationId());
        header.put(MonitoringMessagesKeys.MONITORING_MESSAGE_TIMESTAMP, timestamp);
        Map<String, Object> message = new HashMap<>();
        message.put("header", header);
        message.put("body", messageBody);

        monitoringMessenger.sendMonitoringMessage(
                monitoringServletRequest.getCorrelationId(),
                monitoringServletRequest.getEndpoint(),
                "Send message to backend",
                timestamp,
                timestamp,
                null,
                null,
                messageBody,
                null,
                null,
                null
        );
        String jsonMessage = null;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        LOGGER.debug("Posting message to [" + key + "] " + jsonMessage);
        jedis.rpush(key, jsonMessage);
    }

    private String getEnvironmentValue(String key) {
        return environment.getProperty(key);
    }

}
