package com.octo.monitoring_flux.middleend.monitoring;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A Filter that add response recording features using a MonitoringServletRequest and a ResponseRecordingServletResponse.
 */
public class RecordingFilter implements Filter {

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
        
    	RecordingServletResponse recordingServletResponse = 
        		new RecordingServletResponse((HttpServletResponse) response);
        
        MonitoringServletRequest monitoringServletRequest = 
        		new MonitoringServletRequest((HttpServletRequest) request);
 
        chain.doFilter(monitoringServletRequest, recordingServletResponse);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }
}
