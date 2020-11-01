package com.quartz.scheduler.job;

import java.util.Date;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.quartz.scheduler.service.JobService;
import org.springframework.web.client.RestTemplate;
import static com.quartz.scheduler.util.SchedulerUtil.Constants.*;

@Slf4j
public class CronJob extends QuartzJobBean implements InterruptableJob{
	
	private volatile boolean toStopFlag = true;

	@Autowired
	@Lazy
	private JobService jobService;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		log.info("Cron Job started with key :{},  Group :{}, Group :{} , Thread Name :{}, Time now : {}"
				,key.getName(),key.getGroup(),Thread.currentThread().getName(), new Date());
		
		//*********** For retrieving stored key-value pairs ***********/
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ObjectNode node = (ObjectNode)new ObjectMapper()
					.readTree(dataMap.get(JOB_REQ_BODY).toString());
			restTemplate.postForObject("http://localhost:4567/scheduler/post-test"
					, node, Void.class);
		} catch (RuntimeException | JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		log.info("Thread: {} completed successfully", Thread.currentThread().getName() );
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.info("Stopping thread... ");
		toStopFlag = false;
	}

}