package com.quartz.scheduler.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quartz.scheduler.dto.JobState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import static com.quartz.scheduler.util.SchedulerUtil.Constants.*;

@Service
@Slf4j
@AllArgsConstructor
public class JobServiceImpl implements JobService{

	private SchedulerFactoryBean schedulerFactoryBean;

	private ApplicationContext context;

	private static final String groupKey = "SampleGroup";

	@Override
	public boolean scheduleOneTimeJob(String jobName, Class<? extends QuartzJobBean> jobClass
			,Date jobScheduleTime, Map<String, Object> JobData) {
		log.info("Request received to scheduleJob");

		String jobKey = jobName;
		String triggerKey = jobName;

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.putAll(JobData);

		JobDetail jobDetail = JobUtil.createJob(jobClass, false, context, jobKey, groupKey, jobDataMap);
		log.info("creating trigger for key : {} at date : {} ", jobKey, jobScheduleTime);
		Trigger cronTriggerBean = JobUtil.createSingleTrigger(triggerKey, jobScheduleTime, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
			log.info("Job with key jobKey :{} and group : {} scheduled successfully for date : {} ", jobKey, groupKey, dt);
			return true;
		} catch (SchedulerException e) {
			log.error("SchedulerException while scheduling job with key :{} message: {} ", jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}

		return false;
	}
	

	@Override
	public boolean scheduleCronJob(String jobName, Class<? extends QuartzJobBean> jobClass
			,Date jobScheduleTime, Map<String, Object> JobData) {
		log.info("Request received to scheduleJob");

		String jobKey = jobName;
		String triggerKey = jobName;
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.putAll(JobData);

		JobDetail jobDetail = JobUtil.createJob(jobClass, false, context, jobKey, groupKey, jobDataMap);
		String cronExpression = JobData.get(JOB_CRON_EXP_REQ_PARAM).toString();
		log.info("creating trigger for key : {} at date : {} ", jobKey, jobScheduleTime);
		Trigger cronTriggerBean = JobUtil.createCronTrigger(triggerKey, jobScheduleTime, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
			log.info("Job with key jobKey :{} and group : {} scheduled successfully for date : {} ", jobKey, groupKey, dt);
			return true;
		} catch (SchedulerException e) {
			log.error("SchedulerException while scheduling job with key :{} message: {} ", jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}

		return false;
	}


	@Override
	public boolean updateOneTimeJob(String jobName, Date date) {

		String jobKey = jobName;

		log.info("Parameters received for updating one time job - jobKey : {}, date : {} ", jobKey, date);
		try {
			Trigger newTrigger = JobUtil.createSingleTrigger(jobKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

			Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
			log.info("Trigger associated with jobKey :{}, rescheduled successfully for date :", jobKey, dt);
			return true;
		} catch ( Exception e ) {
			log.error("SchedulerException while scheduling job with key :{} message: {} ", jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public boolean updateCronJob(String jobName, Date date, String cronExpression) {

		String jobKey = jobName;

		log.info("Parameters received for updating one time job - jobKey : {}, date : {} ", jobKey, date);
		try {
			Trigger newTrigger = JobUtil.createCronTrigger(jobKey, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

			Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
			log.info("Trigger associated with jobKey :{}, rescheduled successfully for date :", jobKey, dt);
			return true;
		} catch ( Exception e ) {
			log.error("SchedulerException while scheduling job with key :{} message: {} ", jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}
	

	@Override
	public boolean unScheduleJob(String jobName) {

		String jobKey = jobName;

		TriggerKey tkey = new TriggerKey(jobKey);
		log.info("Parameters received for un-Scheduling job : jobKey : {}", jobKey);
		try {
			boolean status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
			log.info("Trigger associated with jobKey : {}, unscheduled with status :",jobKey, status);
			return status;
		} catch (SchedulerException e) {
			log.error("SchedulerException while un-Scheduling job with key :{}, message: {}",jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public boolean deleteJob(String jobName) {

		String jobKey = jobName;
		String groupKey = "SampleGroup";

		JobKey jkey = new JobKey(jobKey, groupKey); 
		log.info("Parameters received for deleting job : jobKey :{}", jobKey);

		try {
			boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
			log.info("Job with jobKey :{}, deleted with status :",jobKey, status);
			return status;
		} catch (SchedulerException e) {
			log.error("SchedulerException while deleting job with key :{}, message: {}",jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public boolean pauseJob(String jobKey) {

		JobKey jkey = new JobKey(jobKey, groupKey);
		log.info("Parameters received for pausing job - jobKey : {}, groupKey: {} ", jobKey, groupKey);
		try {
			schedulerFactoryBean.getScheduler().pauseJob(jkey);
			log.info("Job with jobKey : {} paused successfully.", jobKey);
			return true;
		} catch (SchedulerException e) {
			log.error("SchedulerException while pausing job with key : {} message {} ", jobKey , e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public boolean resumeJob(String jobName) {

		String jobKey = jobName;

		JobKey jKey = new JobKey(jobKey, groupKey); 
		log.info("Parameters received for resuming job - jobKey :"+jobKey);
		try {
			schedulerFactoryBean.getScheduler().resumeJob(jKey);
			log.info("Job with jobKey :{}, resumed successfully.", jobKey);
			return true;
		} catch (SchedulerException e) {
			log.error("SchedulerException while resuming job with key : {} message {} ", jobKey , e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public boolean startJobNow(String jobName) {

		String jobKey = jobName;

		JobKey jKey = new JobKey(jobKey, groupKey); 
		log.info("Parameters received for starting job now : jobKey :{}", jobKey);
		try {
			schedulerFactoryBean.getScheduler().triggerJob(jKey);
			log.info("Job with jobKey :{}, started now successfully.", jobKey);
			return true;
		} catch (SchedulerException e) {
			log.error("SchedulerException while starting job with key : {} message {} ", jobKey , e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public boolean isJobRunning(String jobName) {

		String jobKey = jobName;

		log.info("Parameters received for checking job is running ? : jobKey :{}", jobKey);
		try {

			List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
			if(currentJobs!=null){
				for (JobExecutionContext jobCtx : currentJobs) {
					String jobNameDB = jobCtx.getJobDetail().getKey().getName();
					String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
					if (jobKey.equalsIgnoreCase(jobNameDB) && groupKey.equalsIgnoreCase(groupNameDB)) {
						return true;
					}
				}
			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while checking job with key :{}, is running. error message :{}", jobKey, e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}


	@Override
	public List<Map<String, Object>> getAllJobs() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();

					//get job's trigger
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					Date scheduleTime = triggers.get(0).getStartTime();
					Date nextFireTime = triggers.get(0).getNextFireTime();
					Date lastFiredTime = triggers.get(0).getPreviousFireTime();
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("jobName", jobName);
					map.put("groupName", jobGroup);
					map.put("scheduleTime", scheduleTime);
					map.put("lastFiredTime", lastFiredTime);
					map.put("nextFireTime", nextFireTime);
					
					if(this.isJobRunning(jobName)){
						map.put("jobStatus", "RUNNING");
					}else{
						JobState jobState = this.getJobState(jobName);
						map.put("jobStatus", jobState.toString());
					}

					list.add(map);
					log.info("***** Job details *****");
					log.info("Job Name: {}, Group Name:{}, Schedule Time:{}",jobName, groupName ,scheduleTime);
				}

			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while fetching all jobs. error message :{}", e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return list;
	}

	@Override
	public boolean isJobWithNamePresent(String jobName) {
		try {
			JobKey jobKey = new JobKey(jobName, groupKey);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)){
				return true;
			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while checking job with name and group exist: {}", e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}

	@Override
	public JobState getJobState(String jobName) {
		log.info("JobServiceImpl.getJobState()");

		try {
			JobKey jobKey = new JobKey(jobName, groupKey);

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);

			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
			if(triggers != null && triggers.size() > 0){
				for (Trigger trigger : triggers) {
					TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					return JobState.toJobState(triggerState);
				}
			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while checking job with name and group exist: {}", e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return JobState.NONE;
	}

	@Override
	public boolean stopJob(String jobName) {
		log.info("JobServiceImpl.stopJob()");
		try{	
			String jobKey = jobName;

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jkey = new JobKey(jobKey, groupKey);

			return scheduler.interrupt(jkey);

		} catch (SchedulerException e) {
			log.error("SchedulerException while stopping job. error message :{}", e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}
}

