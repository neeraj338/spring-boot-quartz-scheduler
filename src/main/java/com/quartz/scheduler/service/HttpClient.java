package com.quartz.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.quartz.scheduler.util.SchedulerUtil.Constants.*;
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
                    , getHttpHeader(node, basicAuthToken), Object.class);
        } catch (Exception e) {
            log.error("Error attempting http call {}", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void getBackendResponseFallback(RuntimeException e) {
        log.info("Recovery - Returning finished .");
        return ;
    }

    private static <T> HttpEntity<?> getHttpHeader(T requestObject, String basicAuthToken) {

        HttpHeaders headers = getHttpHeader(basicAuthToken);
        if(requestObject == null){
            return new HttpEntity<>(headers);
        }

        return new HttpEntity<>(requestObject, headers);
    }

    private static HttpHeaders getHttpHeader(String basicAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(basicAuthToken != null && !basicAuthToken.trim().isEmpty()){
            headers.setBasicAuth(basicAuthToken);
        }
        return headers;
    }
}
