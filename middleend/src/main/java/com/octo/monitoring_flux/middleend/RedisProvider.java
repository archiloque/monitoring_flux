package com.octo.monitoring_flux.middleend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.monitoring_flux.middleend.monitoring.MonitoringServletRequest;
import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis-related functions.
 */
public class RedisProvider {

    private final Jedis jedis;

    private final MonitoringUtilities monitoringUtilities = new MonitoringUtilities();

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisProvider.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisProvider(int redisPort) {
        jedis = new Jedis("localhost", redisPort);
        LOGGER.debug(jedis.ping());
    }

    public void postMessageToBackend(Class sender, HttpServletRequest request, String key, Object messageBody) {
        MonitoringServletRequest monitoringServletRequest = (MonitoringServletRequest) request;

        String timestamp = monitoringUtilities.getTimeStampAsRfc339();
        Map<String, String> header = new HashMap<>();
        header.put("correlation_id", monitoringServletRequest.getCorrelationId());
        header.put("timestamp", timestamp);
        Map<String, Object> message = new HashMap<>();
        message.put("header", header);
        message.put("body", messageBody);
        monitoringUtilities.sendMonitoringMessage(
                sender,
                "Send message to backend",
                timestamp,
                message
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

}
