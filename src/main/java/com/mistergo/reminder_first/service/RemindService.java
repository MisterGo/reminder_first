package com.mistergo.reminder_first.service;

import com.mistergo.reminder_first.model.RemindDto;
import com.mistergo.reminder_first.model.RemindFilterDto;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

/**
 * Сервис для работы с напоминаниями.
 * Создаёт, редактирует, удаляет напоминания и трриггеры, а также управляет списками напоминаний.
 */
public interface RemindService {
    /**
     * Создать напоминание
     */
    RemindDto create(RemindDto remindDto, Principal principal) throws SchedulerException;

    /**
     * Редактировать напоминание
     */
    RemindDto edit(RemindDto remindDto, Principal principal) throws SchedulerException;

    /**
     * Удалить напоминание
     */
    void delete(Long id, Principal principal) throws SchedulerException;

    /**
     * Фильр по напоминаниям
     */
    Page<RemindDto> filter(Pageable pageable, Principal principal, RemindFilterDto filter);

    /**
     * Сортировка напоминаний
     */
    Page<RemindDto> sort(Pageable pageable, Principal principal, String orderBy, boolean desc);

    /**
     * Список напоминаний
     */
    Page<RemindDto> list(Pageable pageable, Principal principal);
}
