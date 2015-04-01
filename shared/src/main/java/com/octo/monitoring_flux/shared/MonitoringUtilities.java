package com.octo.monitoring_flux.shared;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Utilities for monitoring.
 */
public interface MonitoringUtilities {

    /**
     * A rfc-399 formatter for dates.
     */
    static final DateFormat rfc339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static String getLocalhost() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException ignored) {
            return "";
        }
    }

    static final String localhost = getLocalhost();

    /**
     * Get the current timestamp.
     */
    public static Date getCurrentTimestamp() {
        return new Date();
    }
    
    public static long getTimeStampFromRfc339(String expression) {
    	try {
			return rfc339.parse(expression).getTime();
		} catch (ParseException pe) {
			throw new IllegalArgumentException("Expecting yyyy-MM-dd'T'HH:mm:ss.SSSXXX", pe);
		}
    }

    /**
     * Format a date in rfc-399 format.
     * @param date the non-null date.
     * @return the formatted date.
     */
    public static String formatDateAsRfc339(Date date) {
        synchronized (MonitoringUtilities.class) {
            return rfc339.format(date);
        }
    }

    /**
     * Get the current timestamp in a rfc-399 format.
     */
    public static String getCurrentTimestampAsRfc339(){
        return formatDateAsRfc339(getCurrentTimestamp());
    }

    /**
     * Create a new correlation id;
     */
    public static String createCorrelationId() {
        return localhost + "_" + formatDateAsRfc339(getCurrentTimestamp()) + "_" + UUID.randomUUID();
    }

}
