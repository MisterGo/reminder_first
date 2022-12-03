package com.mistergo.reminder_first.quartz;

import com.mistergo.reminder_first.model.RemindDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class RemindQuartzServiceImpl implements RemindQuartzService {
    private final SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void createRemindTrigger(RemindDto remindDto) throws SchedulerException {
        var reminderJobDetail = JobBuilder.newJob()
                .ofType(RemindQuartzJob.class)
                .storeDurably()
                .withIdentity(remindDto.getId().toString())
                .withDescription("Create reminder to specific date and time")
                .build();

        var remindDate = Date.from(remindDto.getRemind().atZone(ZoneId.systemDefault()).toInstant());
        var trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .forJob(reminderJobDetail)
                .withIdentity(remindDto.getId().toString())
                .usingJobData(new JobDataMap(Collections.emptyMap()))
                .startAt(remindDate)
                .build();

        var scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJob(reminderJobDetail, trigger);
        scheduler.start();
    }

    @Override
    public void editRemindTrigger(RemindDto remindDto) throws SchedulerException {
        var scheduler = schedulerFactoryBean.getScheduler();
        var triggerKey = TriggerKey.triggerKey(remindDto.getId().toString());
        if (scheduler.checkExists(triggerKey)) {
            var jobDetail = scheduler.getJobDetail(JobKey.jobKey(remindDto.getId().toString()));
            var remindDate = Date.from(remindDto.getRemind().atZone(ZoneId.systemDefault()).toInstant());
            var newTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(remindDto.getId().toString())
                    .startAt(remindDate)
                    .build();
            scheduler.rescheduleJob(triggerKey, newTrigger);
        }
    }

    @Override
    public void deleteRemindTrigger(Long id) throws SchedulerException {
        var scheduler = schedulerFactoryBean.getScheduler();
        var jobKey = JobKey.jobKey(id.toString());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }
}
