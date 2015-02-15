package com.octo.monitoring_flux.middleend.controller;

import com.octo.monitoring_flux.middleend.RedisProvider;
import com.octo.monitoring_flux.middleend.dto.EndPoint1Response;
import com.octo.monitoring_flux.middleend.dto.EndPoint2Request;
import com.octo.monitoring_flux.middleend.dto.EndPoint2Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@RestController
@PropertySource(value = {"classpath:middleend.properties"})
public class MiddleEndController {

    @Autowired
    private Environment environment;

    private RedisProvider redisProvider;

    private String redisKey;

    @PostConstruct
    private void postConstruct() {
        redisProvider = new RedisProvider(Integer.parseInt(environment.getProperty("redis.port")));
        redisKey = environment.getProperty("redis.key");
    }


    @RequestMapping(
            value = "/endpoint1",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public EndPoint1Response endpoint1() {
        return new EndPoint1Response("OK");
    }

    @RequestMapping(
            value = "/endpoint2",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public EndPoint2Response endpoint2(@RequestBody EndPoint2Request endPoint2Request, HttpServletRequest request) {
        for (int i = 0; i < endPoint2Request.getNumberOfMessages(); i++) {
            redisProvider.postMessageToBackend(getClass(), request, redisKey, endPoint2Request);
        }
        return new EndPoint2Response("OK");
    }
}
