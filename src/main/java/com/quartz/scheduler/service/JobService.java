package com.quartz.scheduler.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.quartz.scheduler.dto.JobState;
import org.springframework.scheduling.quartz.QuartzJobBean;

public interface JobService {
	boolean scheduleOneTimeJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date jobScheduleTime, Map<String, Object> JobData);
	boolean scheduleCronJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date jobScheduleTime, Map<String, Object> JobData);
	
	boolean updateOneTimeJob(String jobName, Date date);
	boolean updateCronJob(String jobName, Date date, String cronExpression);
	
	boolean unScheduleJob(String jobName);
	boolean deleteJob(String jobName);
	boolean pauseJob(String jobName);
	boolean resumeJob(String jobName);
	boolean startJobNow(String jobName);
	boolean isJobRunning(String jobName);
	List<Map<String, Object>> getAllJobs();
	boolean isJobWithNamePresent(String jobName);
	JobState getJobState(String jobName);
	boolean stopJob(String jobName);
}
