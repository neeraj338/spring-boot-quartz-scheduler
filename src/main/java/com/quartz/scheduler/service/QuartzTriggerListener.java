package com.quartz.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QuartzTriggerListener implements TriggerListener {

    @Override
    public String getName() {
        return "globalTrigger";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
    	log.info("TriggerListner.triggerFired()");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    	log.info("TriggerListner.vetoJobExecution()");
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
    	log.info("TriggerListner.triggerMisfired()");
        String jobName = trigger.getJobKey().getName();
        log.info("Job name: " + jobName + " is misfired");
        
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        log.info("TriggerListner.triggerComplete()");
    }
}
