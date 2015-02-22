package com.octo.monitoring_flux.middleend;

import com.octo.monitoring_flux.middleend.monitoring.MonitoringInterceptor;
import com.octo.monitoring_flux.middleend.monitoring.RecordingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;

/**
 * Base class for the application, used by spring boot.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource(value = {"classpath:middleend.properties"})
public class Application extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;

    private String appName;

    private String appId;

    private int zeromqPort;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    private void postConstruct() {
        appName = environment.getProperty("app.name");
        appId = appName + "." + ManagementFactory.getRuntimeMXBean().getName();
        zeromqPort = Integer.parseInt(environment.getProperty("zeromq.port"));
    }

    /**
     * Add the MonitoringInterceptor.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new MonitoringInterceptor(appName, appId, zeromqPort)
        );
    }

    /**
     * Register the ResponseRecordingFilter.
     */
    @Bean
    public FilterRegistrationBean monitoringFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RecordingFilter());
        return registration;
    }


}
