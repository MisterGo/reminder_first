package com.mistergo.reminder_first.quartz;

import com.mistergo.reminder_first.model.RemindDto;
import org.quartz.SchedulerException;

/**
 * Сервис для управления триггерами
 */
public interface RemindQuartzService {
    /**
     * Создаёт триггер
     * @param remindDto dto напоминания
     * @throws SchedulerException
     */
    void createRemindTrigger(RemindDto remindDto) throws SchedulerException;

    /**
     * Резактирует триггер
     * @param remindDto dto напоминания
     * @throws SchedulerException
     */
    void editRemindTrigger(RemindDto remindDto) throws SchedulerException;

    /**
     * Удаляет триггер
     * @param id id напоминания
     * @throws SchedulerException
     */
    void deleteRemindTrigger(Long id) throws SchedulerException;
}
