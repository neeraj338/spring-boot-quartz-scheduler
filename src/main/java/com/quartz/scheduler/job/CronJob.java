package com.quartz.scheduler.job;

import java.util.Date;


import com.quartz.scheduler.service.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.quartz.scheduler.service.JobService;
import static com.quartz.scheduler.util.SchedulerUtil.Constants.*;

@Slf4j
public class CronJob extends QuartzJobBean implements InterruptableJob{
	
	private volatile boolean toStopFlag = true;

	@Autowired
	@Lazy
	private JobService jobService;

	@Autowired
	@Lazy
	private HttpClient httpClient;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		log.info("Cron Job started with key :{},  Group :{}, Group :{} , Thread Name :{}, Time now : {}"
				,key.getName(),key.getGroup(),Thread.currentThread().getName(), new Date());
		
		//*********** For retrieving stored key-value pairs ***********/
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
		String url = dataMap.get(HTTP_URL_ENDPOINT).toString();
		HttpMethod httpMethod = HttpMethod.valueOf(dataMap.get(HTTP_METHOD).toString());
		httpClient.exchange(url, httpMethod, dataMap);
		log.info("Thread: {} completed successfully", Thread.currentThread().getName() );
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.info("Stopping thread... ");
		toStopFlag = false;
	}

}