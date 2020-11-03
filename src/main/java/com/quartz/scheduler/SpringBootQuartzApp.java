package com.quartz.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SpringBootQuartzApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootQuartzApp.class);
	}
}
