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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Add the MonitoringInterceptor.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MonitoringInterceptor(Integer.parseInt(environment.getProperty("zeromq.port"))));
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
