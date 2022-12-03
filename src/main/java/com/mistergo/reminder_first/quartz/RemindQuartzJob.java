package com.mistergo.reminder_first.quartz;

import com.mistergo.reminder_first.data.repository.RemindRepository;
import com.mistergo.reminder_first.sender.EmailSender;
import com.mistergo.reminder_first.sender.TelegramSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

/**
 * Сервис обработки сработавших триггеров
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RemindQuartzJob implements Job {
    private final RemindRepository remindRepository;
    private final EmailSender emailSender;
    private final TelegramSender telegramSender;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var key = context.getJobDetail().getKey().getName();

        var remindEntity = remindRepository.findById(Long.valueOf(key))
                .orElseThrow(() -> new JobExecutionException("Can not find Remind entity"));
        var userEntity = remindEntity.getUser();
        if (userEntity == null) {
            throw new JobExecutionException("Can not find User entity");
        }

        var email = userEntity.getEmail();
        var telegramId = userEntity.getTelegramId();
        try {
            if (email != null) {
                emailSender.sendEmailMessage(email, remindEntity.getTitle(), remindEntity.getDescription());
            }
            if (telegramId != null) {
                telegramSender.sendTelegramMessage(telegramId, remindEntity.getTitle(), remindEntity.getDescription());
            }
        } catch (Exception e) {
            // не ломаемся, если не смогли отправить сообщени
            log.error("Can not send message. Error:\n" + e.getMessage());
        }
    }
}
