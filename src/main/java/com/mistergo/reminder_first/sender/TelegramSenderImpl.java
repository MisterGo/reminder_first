package com.mistergo.reminder_first.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramSenderImpl implements TelegramSender {
    private final String TELEGRAM_BASE_URL = "https://api.telegram.org/bot";
    private final String TELEGRAM_TOKEN = "761411464:AAHr16X-f5L0umvf9pU6HeONz1oQ2hiRUl8";
    private final String API_URL = TELEGRAM_BASE_URL + TELEGRAM_TOKEN;

    //todo API telegram не позволяет отправлять сообщения по @UserId. Требуется реализовать получение chat_id по @UserId
    @Override
    public void sendTelegramMessage(String telegramId, String title, String message) throws Exception {
        log.info("Sending telegram message to user: {}, title: {}, message: {}", telegramId, title, message);

        try {
            RestTemplate restTemplate = new RestTemplate();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL + "/sendMessage")
                    .queryParam("chat_id", telegramId)
                    .queryParam("text", "**" + title + "**\n\n" + message);
            var exchange = restTemplate.exchange(builder.toUriString().replaceAll("%20", " "), HttpMethod.GET, null, String.class);
            log.info("Telegram message sent");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error response : Status code: {}, response: {} ", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception err) {
            log.error("Error: {} ", err.getMessage());
            throw new Exception("Telegram service is not available at the moment!");
        }
    }
}
