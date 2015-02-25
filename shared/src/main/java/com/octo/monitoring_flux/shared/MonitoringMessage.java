package com.octo.monitoring_flux.shared;

import java.io.Serializable;
import java.util.Map;

/**
 * POJO representing a monitoring message.
 *
 * @author clunven
 */
public class MonitoringMessage implements Serializable {
	
	/** Serial. */
	private static final long serialVersionUID = -5450039726854700100L;

	/** Correlation identifier. */
	private String correlationId;
	
	/** Producer endpoint. */
	private String endPoint;
	
	/** Message timestamp. */
	private String timeStamp;
	
	/** start date. */
	private String beginTimestamp;
	
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

}
