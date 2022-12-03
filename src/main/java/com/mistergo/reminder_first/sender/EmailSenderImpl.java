package com.mistergo.reminder_first.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderImpl implements EmailSender {
    private final JavaMailSender mailSender;
    @Override
    public void sendEmailMessage(String email, String title, String message) {
        log.info("Sending email message to address: {}, title: {}, message: {}", email, title, message);

        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("gogolev@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject(title);
        mailMessage.setText(message);
        mailSender.send(mailMessage);

        log.info("Email message sent");
    }
}
