package com.quartz.scheduler.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import com.quartz.scheduler.service.JobsListener;
import com.quartz.scheduler.service.QuartzTriggerListener;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@AllArgsConstructor
public class QuartzSchedulerConfig {
 
    private DataSource dataSource;
 
    private ApplicationContext applicationContext;
     
    private QuartzTriggerListener triggerListener;

    private JobsListener jobsListener;
    

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
 
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(quartzProperties());
        
        //Register listeners to get notification on Trigger misfire etc
        factory.setGlobalTriggerListeners(triggerListener);
        factory.setGlobalJobListeners(jobsListener);
        
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);
        
        return factory;
    }
 

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
 
  
}