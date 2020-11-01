package com.quartz.scheduler.dto;

import lombok.NonNull;
import org.quartz.Trigger;

import java.util.stream.Stream;

public enum JobState {
        NONE("NONE"),
        SCHEDULED("NORMAL"),
        PAUSED("PAUSED"),
        COMPLETE("COMPLETE"),
        ERROR("ERROR"),
        BLOCKED("BLOCKED");

        private String triggerState;

        JobState(String triggerState){
            this.triggerState = triggerState;
        }

        @NonNull
        public static JobState toJobState(Trigger.TriggerState triggerState){
            JobState state = Stream.of(JobState.values())
                    .filter(jobState -> jobState.triggerState.equalsIgnoreCase(triggerState.toString()))
                    .findFirst()
                    .orElse(NONE);
            return state;
        }

        @NonNull
        public Trigger.TriggerState triggerState(){
            return Trigger.TriggerState.valueOf(this.triggerState);
        }
}
