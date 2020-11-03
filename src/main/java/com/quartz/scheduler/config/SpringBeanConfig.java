package com.quartz.scheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class SpringBeanConfig {

    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
