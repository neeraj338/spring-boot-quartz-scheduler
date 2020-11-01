package com.quartz.scheduler.job;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.quartz.scheduler.service.JobService;

@Slf4j
public class SimpleJob extends QuartzJobBean implements InterruptableJob{
	
	private volatile boolean toStopFlag = true;

	@Autowired
	@Lazy
	private JobService jobService;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		log.info("Simple Job started with key :" + key.getName() + ", Group :"+key.getGroup() + " , Thread Name :"+Thread.currentThread().getName());
		
		log.info("======================================");
		log.info("Accessing annotation example: "+jobService.getAllJobs());
		List<Map<String, Object>> list = jobService.getAllJobs();
		log.info("Job list :"+list);
		log.info("======================================");
		
		//*********** For retrieving stored key-value pairs ***********/
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

		//*********** For retrieving stored object, It will try to deserialize the bytes Object. ***********/
		/*
		SchedulerContext schedulerContext = null;
        try {
            schedulerContext = jobExecutionContext.getScheduler().getContext();
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }
        YourClass yourClassObject = (YourClass) schedulerContext.get("storedObjectKey");
		 */

		while(toStopFlag){
			try {
				log.info("Test Job Running... Thread Name :"+Thread.currentThread().getName());
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info("Thread: "+ Thread.currentThread().getName() +" stopped.");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.info("Stopping thread... ");
		toStopFlag = false;
	}

}