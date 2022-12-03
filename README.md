# Приложение Reminder

## TODO (что пока не сделано)
* Авторизацие через Oauth2 - не смог победить ошибку с redirect_url
* Отправка сообщений в Telegram - в последний момент понял, что нет возможности отправлять сообщения по `@userId`. Нужно сначала вытаскивать для пользователя `chat_id`

## Настройка приложения
В файле `appplication.yml` настроить поля для отправки email:
* `spring.mail.host`
* `spring.mail.port`
* `spring.mail.username`
* `spring.mail.password`

## Сборка приложения
    ./gradlew clean build

## Запуск приложения
    docker-compose up --build
Приложение будет доступно по адресу `localhost:8080`

## Тестирование приложения (Например с помощью Postman)
Все запросы выполняются с Basic аутентификацией. Варианты логинов: `admin/pass`, `user/pass`
### Создать напоминание
Отправить POST запрос `localhost:8080/api/v1/reminder/create` с телом:
```
{
    "title":"Title 3",
    "description":"desc 3",
    "remind":"2022-12-04 10:03:00"
}
```

### Редактировать напоминание
Отправить POST запрос `localhost:8080/api/v1/reminder/edit` с телом:
```
{
    "id":2,
    "title":"Edited title 2",
    "description":"Edited desc admin 2",
    "remind":"2022-12-03 10:10:00"
}
```

### Получить список напоминаний с фильтром по дате+времени
Отправить POST запрос `localhost:8080/api/v1/reminder/filter` с телом:
```
{
    "dateTimeFrom":"2022-12-04 10:01:00",
    "dateTimeTo":"2022-12-04 10:02:00"
}
```

### Получить список напоминаний
Отправить GET запрос `localhost:8080/api/v1/reminder/list`

### Отсортировать список напоминаний

Отправить GET запрос. Примеры: 

`localhost:8080/api/v1/reminder/sort?orderBy=remind` - сортировка по дате+времени

`localhost:8080/api/v1/reminder/sort?orderBy=remind&desc=true` - обратная сортировка по дате+времени

`localhost:8080/api/v1/reminder/sort?orderBy=title&desc=true` - обратная сортировка по заголовку


