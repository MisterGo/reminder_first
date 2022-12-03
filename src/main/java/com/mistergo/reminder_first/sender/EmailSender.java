package com.mistergo.reminder_first.sender;

/**
 * Сервис отправки email
 */
public interface EmailSender {
    /**
     * Отправляет сообщение на указанный email
     * @param email адрес получателя
     * @param title заголовок
     * @param message текст сообщения
     */
    void sendEmailMessage(String email, String title, String message);
}
