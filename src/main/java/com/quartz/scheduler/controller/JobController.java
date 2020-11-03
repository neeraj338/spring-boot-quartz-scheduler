package com.quartz.scheduler.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.quartz.scheduler.dto.JobState;
import com.quartz.scheduler.dto.ServerResponse;
import com.quartz.scheduler.job.CronJob;
import com.quartz.scheduler.job.OneTimeJob;
import com.quartz.scheduler.service.JobService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import static com.quartz.scheduler.util.SchedulerUtil.Constants.*;
import com.quartz.scheduler.util.ServerResponseCode;

import javax.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/scheduler/")
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:8080","\"http://localhost:3000\""}
, allowedHeaders = {"*"}
, methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST,RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.DELETE})
public class JobController {

	private JobService jobService;

	@PostMapping(value = "/post-test", consumes = {MediaType.ALL_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public void scheduleTest(){
		log.info("****** testing rocks  ***");
		log.info("json string is  {}");
	}

	@PostMapping(value = "schedule", consumes = {MediaType.ALL_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ServerResponse schedule(@RequestParam(value = JOB_NAME_REQ_PARAM) String jobName,
								   @RequestParam(value = JOB_SCHEDULE_TIME) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date jobScheduleTime,
								   @RequestParam(value = JOB_CRON_EXP_REQ_PARAM, required = false) String cronExpression,
								   @RequestParam(value = HTTP_URL_ENDPOINT) String url,
								   @RequestParam(value = HTTP_METHOD, defaultValue = "GET") HttpMethod httpMethod,
								   @RequestParam(value = BASIC_AUTH_TOKEN, required = false) String basicAuthToken,
								   @RequestBody(required = false) JsonNode jsonBody){
		log.info("JobController.schedule()");
		Map<String, Object> jobData = buildJobData(jobName, url
				, httpMethod, basicAuthToken, cronExpression, jobScheduleTime, jsonBody);

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}

		//Check if job Name is unique;
		if(!jobService.isJobWithNamePresent(jobName)){

			if(cronExpression == null || cronExpression.trim().equals("")){
				//Single Trigger
				boolean status = jobService.scheduleOneTimeJob(jobName, OneTimeJob.class, jobScheduleTime, jobData);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			}else{
				//Cron Trigger
				boolean status = jobService.scheduleCronJob(jobName, CronJob.class, jobScheduleTime, jobData);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}				
			}
		}else{
			return getServerResponse(ServerResponseCode.JOB_WITH_SAME_NAME_EXIST, false);
		}
	}

	private Map<String, Object> buildJobData(String jobName, String url, HttpMethod httpMethod,
											 String basicAuthToken,
											 String cronExpression, Date jobScheduleTime,
											 JsonNode jsonBody) {
		Map<String, Object> jobData = new HashMap<>();
		jobData.put(JOB_NAME_REQ_PARAM, jobName);
		jobData.put(JOB_CRON_EXP_REQ_PARAM, cronExpression);
		jobData.put(JOB_REQ_BODY, jsonBody.toPrettyString());
		jobData.put(HTTP_URL_ENDPOINT, url);
		jobData.put(HTTP_METHOD, httpMethod.toString());
		jobData.put(BASIC_AUTH_TOKEN, basicAuthToken);
		jobData.put(JOB_SCHEDULE_TIME, jobScheduleTime.toString());

		return jobData;
	}


	@PatchMapping("un-schedule")
	public void unschedule(@RequestParam("jobName") String jobName) {
		log.info("JobController.unschedule()");
		jobService.unScheduleJob(jobName);
	}

	@DeleteMapping("delete")
	public ServerResponse delete(@RequestParam("jobName") String jobName) {
		log.info("JobController.delete()");

		if(jobService.isJobWithNamePresent(jobName)){
			boolean isJobRunning = jobService.isJobRunning(jobName);

			if(!isJobRunning){
				boolean status = jobService.deleteJob(jobName);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
			}else{
				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}
		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@PatchMapping("pause")
	public ServerResponse pause(@RequestParam("jobName") String jobName) {
		log.info("JobController.pause()");

		if(jobService.isJobWithNamePresent(jobName)){

			boolean isJobRunning = jobService.isJobRunning(jobName);

			if(!isJobRunning){
				boolean status = jobService.pauseJob(jobName);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}			
			}else{
				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}		
	}

	@PatchMapping("resume")
	public ServerResponse resume(@RequestParam("jobName") String jobName) {
		log.info("JobController.resume()");

		if(jobService.isJobWithNamePresent(jobName)){
			JobState jobState = jobService.getJobState(jobName);

			if(jobState == JobState.PAUSED){
				log.info("Job current state is PAUSED, Resuming job...");
				boolean status = jobService.resumeJob(jobName);

				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
			}else{
				return getServerResponse(ServerResponseCode.JOB_NOT_IN_PAUSED_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@PutMapping("update")
	public ServerResponse updateJob(@RequestParam("jobName") String jobName, 
			@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime, 
			@RequestParam("cronExpression") String cronExpression){
		log.info("JobController.updateJob()");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}

		//Edit Job
		if(jobService.isJobWithNamePresent(jobName)){
			
			if(cronExpression == null || cronExpression.trim().equals("")){
				//Single Trigger
				boolean status = jobService.updateOneTimeJob(jobName, jobScheduleTime);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			}else{
				//Cron Trigger
				boolean status = jobService.updateCronJob(jobName, jobScheduleTime, cronExpression);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}				
			}
			
			
		}else{
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@GetMapping("jobs")
	public ServerResponse getAllJobs(){
		log.info("JobController.getAllJobs()");

		List<Map<String, Object>> list = jobService.getAllJobs();
		return getServerResponse(ServerResponseCode.SUCCESS, list);
	}

	@GetMapping("checkJobName")
	public ServerResponse checkJobName(@RequestParam("jobName") String jobName){
		log.info("JobController.checkJobName()");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
		
		boolean status = jobService.isJobWithNamePresent(jobName);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@GetMapping("isJobRunning")
	public ServerResponse isJobRunning(@RequestParam("jobName") String jobName) {
		log.info("JobController.isJobRunning()");

		boolean status = jobService.isJobRunning(jobName);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@GetMapping("jobState")
	public ServerResponse getJobState(@RequestParam("jobName") String jobName) {
		log.info("JobController.getJobState()");

		JobState jobState = jobService.getJobState(jobName);
		return getServerResponse(ServerResponseCode.SUCCESS, jobState);
	}

	@PatchMapping("stop")
	public ServerResponse stopJob(@RequestParam("jobName") String jobName) {
		log.info("JobController.stopJob()");

		if(jobService.isJobWithNamePresent(jobName)){

			if(jobService.isJobRunning(jobName)){
				boolean status = jobService.stopJob(jobName);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					//Server error
					return getServerResponse(ServerResponseCode.ERROR, false);
				}

			}else{
				//Job not in running state
				return getServerResponse(ServerResponseCode.JOB_NOT_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@PatchMapping("start")
	public ServerResponse startJobNow(@RequestParam("jobName") String jobName) {
		log.info("JobController.startJobNow()");

		if(jobService.isJobWithNamePresent(jobName)){

			if(!jobService.isJobRunning(jobName)){
				boolean status = jobService.startJobNow(jobName);

				if(status){
					//Success
					return getServerResponse(ServerResponseCode.SUCCESS, true);

				}else{
					//Server error
					return getServerResponse(ServerResponseCode.ERROR, false);
				}

			}else{
				//Job already running
				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	private ServerResponse getServerResponse(int responseCode, Object data){
		ServerResponse serverResponse = new ServerResponse();
		serverResponse.setStatusCode(responseCode);
		serverResponse.setData(data);
		return serverResponse; 
	}

}
