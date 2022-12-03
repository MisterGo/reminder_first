package com.mistergo.reminder_first.sender;

/**
 * Сервис отправки сообщения через telegram
 */
public interface TelegramSender {
    /**
     * Отправляет сообщение через телеграм
     * @param telegramId имя получателя
     * @param title заголовок
     * @param message текст сообщения
     * @throws Exception
     */
    void sendTelegramMessage(String telegramId, String title, String message) throws Exception;
}
