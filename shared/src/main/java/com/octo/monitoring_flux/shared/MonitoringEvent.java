package com.octo.monitoring_flux.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO representing a monitoring message.
 *
 */
public class MonitoringEvent implements Serializable, MonitoringMessagesKeys {

    /**
     * Correlation identifier.
     */
	private String correlationId;
	
	/**
     * Producer endpoint.
     */
	private String endPoint;

    /**
     * Module type.
     */
    private String moduleType;

    /**
     * Module id.
     */
	private String moduleId;

    /**
     * Message timestamp.
     */
	private String timeStamp;

    /**
     * Message type.
     */
	private String messageType;

    /**
     * Start date.
     */
	private String beginTimestamp;

    /**
     * End date.
     */
	private String endTimestamp;

    /**
     * Action duration
     */
	private Double elapsedTime;

    /**
     * Parameters.
     */
	private Map<String, Object> params;

    /**
     * Headers.
     */
	private Map<String, Object> headers;

    /**
     * Result.
     */
	private Object result;

    /**
     * Body.
     */
	private Map<String, Object> initialContent;
	
	/**
	 * Serialize as Object.
	 */
	public Map < String, Object> asMessage() {
		  Map<String, Object> message = new HashMap<>((initialContent== null) ? Collections.emptyMap() : initialContent);
		  if (correlationId != null) message.put(MONITORING_MESSAGE_CORRELATION_ID, correlationId);
		  if (endPoint != null) message.put(MONITORING_MESSAGE_ENDPOINT, endPoint);
		  if (correlationId != null) message.put(MONITORING_MESSAGE_MODULE_TYPE, moduleType);
		  if (correlationId != null) message.put(MONITORING_MESSAGE_MODULE_ID, moduleId);
		  if (correlationId != null) message.put(MONITORING_MESSAGE_MESSAGE_TYPE, messageType);
		  if (correlationId != null) message.put(MONITORING_MESSAGE_TIMESTAMP, timeStamp);
		  if (beginTimestamp != null) message.put(MONITORING_MESSAGE_BEGIN_TIMESTAMP, beginTimestamp);
		  if (endTimestamp != null) message.put(MONITORING_MESSAGE_END_TIMESTAMP, endTimestamp);
          if (elapsedTime != null) message.put(MONITORING_MESSAGE_ELAPSED_TIME, elapsedTime);
          if (params != null) message.put(MONITORING_MESSAGE_PARAMS, params);
          if (headers != null) message.put(MONITORING_MESSAGE_HEADERS, headers);
          if (result != null) message.put(MONITORING_MESSAGE_RESULT, result);
          return message;
	}
	

	/**
	 * Marshalling from JSON Object.
	 */
	public MonitoringEvent(Map<String, Object> msg) {
		if (msg == null) return;
		correlationId 	= (msg.containsKey(MONITORING_MESSAGE_CORRELATION_ID)) ? (String) msg.get(MONITORING_MESSAGE_CORRELATION_ID) : null;
		endPoint 		= (msg.containsKey(MONITORING_MESSAGE_ENDPOINT)) ? (String) msg.get(MONITORING_MESSAGE_ENDPOINT) : null;
		moduleType 		= (msg.containsKey(MONITORING_MESSAGE_MODULE_TYPE)) ? (String) msg.get(MONITORING_MESSAGE_MODULE_TYPE) : null;
		moduleId 		= (msg.containsKey(MONITORING_MESSAGE_MODULE_ID)) ? (String) msg.get(MONITORING_MESSAGE_MODULE_ID) : null;
		messageType		= (msg.containsKey(MONITORING_MESSAGE_MESSAGE_TYPE)) ? (String) msg.get(MONITORING_MESSAGE_MESSAGE_TYPE) : null;
		timeStamp 		= (msg.containsKey(MONITORING_MESSAGE_TIMESTAMP)) ? (String) msg.get(MONITORING_MESSAGE_TIMESTAMP) : null;
		beginTimestamp 	= (msg.containsKey(MONITORING_MESSAGE_BEGIN_TIMESTAMP)) ? (String) msg.get(MONITORING_MESSAGE_BEGIN_TIMESTAMP) : null;
		endTimestamp 	= (msg.containsKey(MONITORING_MESSAGE_END_TIMESTAMP)) ? (String) msg.get(MONITORING_MESSAGE_END_TIMESTAMP) : null;
		elapsedTime 	= (msg.containsKey(MONITORING_MESSAGE_ELAPSED_TIME)) ? (Double) msg.get(MONITORING_MESSAGE_ELAPSED_TIME) : null;
		params 			= (msg.containsKey(MONITORING_MESSAGE_PARAMS)) ? (Map<String, Object>) msg.get(MONITORING_MESSAGE_PARAMS) : null;
		headers 		= (msg.containsKey(MONITORING_MESSAGE_HEADERS)) ? (Map<String, Object>) msg.get(MONITORING_MESSAGE_HEADERS) : null;
		result 			= (msg.containsKey(MONITORING_MESSAGE_RESULT)) ? msg.get(MONITORING_MESSAGE_RESULT) : null;
		initialContent  = msg;
	}
	
	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getBeginTimestamp() {
		return beginTimestamp;
	}

	public void setBeginTimestamp(String beginTimestamp) {
		this.beginTimestamp = beginTimestamp;
	}

	public String getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(String endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public Double getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Map<String, Object> getInitialContent() {
		return initialContent;
	}

	public void setInitialContent(Map<String, Object> initialContent) {
		this.initialContent = initialContent;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

    public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
