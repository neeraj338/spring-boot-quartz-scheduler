package com.quartz.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.quartz.scheduler.util.SchedulerUtil.Constants.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class HttpClient {

    @Lookup
    public RestTemplate restTemplate(){
        return null;
    }

    @Retryable(maxAttempts = 3,value=RuntimeException.class,backoff = @Backoff(delay = 10000, multiplier=2))
    public void exchange(String url, HttpMethod httpMethod, Map<String, Object> jobData){
        try {
            Object jsonBody = jobData.get(JOB_REQ_BODY);
            ObjectNode node = null != jsonBody ? (ObjectNode)new ObjectMapper()
                    .readTree(jsonBody.toString()) : null;
            String basicAuthToken = jobData.getOrDefault(BASIC_AUTH_TOKEN,"").toString();
            restTemplate().exchange(url, httpMethod
                    , buildEntity(node, basicAuthToken), String.class);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void getBackendResponseFallback(RuntimeException e) {
        log.warn("Recovery - Returning finished .");
        log.error("Error attempting http call {}", ExceptionUtils.getStackTrace(e));
        return ;
    }

    private static <T> HttpEntity<?> buildEntity(T requestObject, String basicAuthToken) {

        HttpHeaders headers = buildHeader(basicAuthToken);
        if(requestObject == null){
            return new HttpEntity<>(headers);
        }

        return new HttpEntity<>(requestObject, headers);
    }

    private static HttpHeaders buildHeader(String basicAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(Arrays.asList(StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8, StandardCharsets.UTF_16));
        if(basicAuthToken != null && !basicAuthToken.trim().isEmpty()){
            headers.setBasicAuth(basicAuthToken);
        }
        return headers;
    }
}
