package com.octo.monitoring_flux.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO representing a monitoring message.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class MonitoringMessage implements Serializable, MonitoringMessagesKeys {
	
	/** Serial. */
	private static final long serialVersionUID = -5450039726854700100L;

	/** Correlation identifier. */
	private String correlationId;
	
	/** Producer endpoint. */
	private String endPoint;
	
	private String moduleType;
	
	private String moduleId;
	
	/** Message timestamp. */
	private String timeStamp;
	
	/** Message type. */
	private String messageType;
	
	/** start date. */
	private String beginTimestamp;
	
	/** end date. */
	private String endTimestamp;
	
	/** action duration. */
	private Double elapsedTime;
	
	/** parameters. */
	private Object params;
	
	/** headers. */
	private Object headers;
	
	/** method results. */
	private Object result;
	
	/** body. */
	private Map<String, Object> initialContent;
	
	/**
	 * Serialize as Object.
	 *
	 * @return
	 * 		target object
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
	 * Default Constructor.
	 */
	public MonitoringMessage() {
	}
	
	/**
	 * Marshalling from JSON Object.
	 *
	 * @param message
	 */
	public MonitoringMessage(Map<String, Object> msg) {
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
		params 			= (msg.containsKey(MONITORING_MESSAGE_PARAMS)) ? (String) msg.get(MONITORING_MESSAGE_PARAMS) : null;
		headers 		= (msg.containsKey(MONITORING_MESSAGE_HEADERS)) ? (String) msg.get(MONITORING_MESSAGE_HEADERS) : null;
		result 			= (msg.containsKey(MONITORING_MESSAGE_RESULT)) ? (String) msg.get(MONITORING_MESSAGE_RESULT) : null;
		initialContent  = msg;
	}
	
	/**
	 * Getter accessor for attribute 'correlationId'.
	 *
	 * @return
	 *       current value of 'correlationId'
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * Setter accessor for attribute 'correlationId'.
	 * @param correlationId
	 * 		new value for 'correlationId '
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	/**
	 * Getter accessor for attribute 'endPoint'.
	 *
	 * @return
	 *       current value of 'endPoint'
	 */
	public String getEndPoint() {
		return endPoint;
	}

	/**
	 * Setter accessor for attribute 'endPoint'.
	 * @param endPoint
	 * 		new value for 'endPoint '
	 */
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Getter accessor for attribute 'timeStamp'.
	 *
	 * @return
	 *       current value of 'timeStamp'
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Setter accessor for attribute 'timeStamp'.
	 * @param timeStamp
	 * 		new value for 'timeStamp '
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * Getter accessor for attribute 'beginTimestamp'.
	 *
	 * @return
	 *       current value of 'beginTimestamp'
	 */
	public String getBeginTimestamp() {
		return beginTimestamp;
	}

	/**
	 * Setter accessor for attribute 'beginTimestamp'.
	 * @param beginTimestamp
	 * 		new value for 'beginTimestamp '
	 */
	public void setBeginTimestamp(String beginTimestamp) {
		this.beginTimestamp = beginTimestamp;
	}

	/**
	 * Getter accessor for attribute 'endTimestamp'.
	 *
	 * @return
	 *       current value of 'endTimestamp'
	 */
	public String getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * Setter accessor for attribute 'endTimestamp'.
	 * @param endTimestamp
	 * 		new value for 'endTimestamp '
	 */
	public void setEndTimestamp(String endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	/**
	 * Getter accessor for attribute 'elapsedTime'.
	 *
	 * @return
	 *       current value of 'elapsedTime'
	 */
	public Double getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Setter accessor for attribute 'elapsedTime'.
	 * @param elapsedTime
	 * 		new value for 'elapsedTime '
	 */
	public void setElapsedTime(Double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	/**
	 * Getter accessor for attribute 'params'.
	 *
	 * @return
	 *       current value of 'params'
	 */
	public Object getParams() {
		return params;
	}

	/**
	 * Setter accessor for attribute 'params'.
	 * @param params
	 * 		new value for 'params '
	 */
	public void setParams(Object params) {
		this.params = params;
	}

	/**
	 * Getter accessor for attribute 'headers'.
	 *
	 * @return
	 *       current value of 'headers'
	 */
	public Object getHeaders() {
		return headers;
	}

	/**
	 * Setter accessor for attribute 'headers'.
	 * @param headers
	 * 		new value for 'headers '
	 */
	public void setHeaders(Object headers) {
		this.headers = headers;
	}

	/**
	 * Getter accessor for attribute 'result'.
	 *
	 * @return
	 *       current value of 'result'
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * Setter accessor for attribute 'result'.
	 * @param result
	 * 		new value for 'result '
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * Getter accessor for attribute 'initialContent'.
	 *
	 * @return
	 *       current value of 'initialContent'
	 */
	public Map<String, Object> getInitialContent() {
		return initialContent;
	}

	/**
	 * Setter accessor for attribute 'initialContent'.
	 * @param initialContent
	 * 		new value for 'initialContent '
	 */
	public void setInitialContent(Map<String, Object> initialContent) {
		this.initialContent = initialContent;
	}

	/**
	 * Getter accessor for attribute 'messageType'.
	 *
	 * @return
	 *       current value of 'messageType'
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * Setter accessor for attribute 'messageType'.
	 * @param messageType
	 * 		new value for 'messageType '
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}


	/**
	 * Getter accessor for attribute 'moduleType'.
	 *
	 * @return
	 *       current value of 'moduleType'
	 */
	public String getModuleType() {
		return moduleType;
	}


	/**
	 * Setter accessor for attribute 'moduleType'.
	 * @param moduleType
	 * 		new value for 'moduleType '
	 */
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}


	/**
	 * Getter accessor for attribute 'moduleId'.
	 *
	 * @return
	 *       current value of 'moduleId'
	 */
	public String getModuleId() {
		return moduleId;
	}


	/**
	 * Setter accessor for attribute 'moduleId'.
	 * @param moduleId
	 * 		new value for 'moduleId '
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}
