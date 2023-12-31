# java-shareit
Template repository for ShareIt project.

<picture>
    <source media="(prefers-color-scheme: dark)" srcset="/ShareItDB v1.1.png">
    <img src="/ShareItDB v1.1.png">
</picture>

## Приложение представляет из себя
### ShareIt - приложение, для шеринга вещей.

## В ходе проекта реализованы следующие функции

### Пользователь
#### API:
- GET /users - возвращает список DTO всех пользователей.
- GET /users/{userId} - возвращает DTO пользователя с переданный id.
- POST /users - принимает DTO пользователя, создает нового пользователя, записывет его в БД.
- PATCH /users/{userId} - принимает DTO пользователя, id пользователя. Меняет необходимые поля в пользователя с заданным id.
- DELETE /users/{userId} - удаляет пользователя с заданным id.

### Вещь
#### API:
- GET /items - возвращает DTO всех вещей пользователя, id которого было передано заголовком "X-Sharer-User-Id".
- GET /items/{itemId} - возвращает DTO вещи с переданным id. Если пользователь, который сделал запрос является владельцем вещи, вернется вещь с полями: последнее бронирование, следующее бронирование.
- GET /items/search - возвращает DTO вещи, в имени или описании которой есть заданный в @RequestParam текст.
- POST /items - принимает DTO вещи, создает новую вещь, записывает данные о ней в БД.
- PATCH /items/{itemId} - принимает DTO вещи, id вещи. Меняет необходимые поля в вещи с заданным id.
- DELETE /items/{itemId} - удаляет вещь с заданным id.
- POST /items/{itemId}/comment - принимает DTO комментария, id вещи. Создает комментарий, записывет его в БД.

### Бронирование
#### API:
- POST /bookings - принимает DTO бронирования. Создает бронирование от лица пользователя, сделавшего запрос, записывает бронирование в БД.
- PATCH /bookings{bookingId} - принимает id бронирования и логическую переменную в виде @RequestParam. В записимости от хначения переменной меняет статус бронирования. Доступно только для владельца вещи, на которую заведено данное бронирование.
- GET /bookings/{bookingId} - возвращает DTO бронирования с заданным id.
- GET /bookings - возвращает список DTO бронирований пользователя сделавшего запрос.
- GET /bookings/owner - возвращает список DTO бронирований на все вещи пользователя, сделавшего запрос.

### Запрос
#### API:
- POST /requests - принимает DTO запроса. Создает запрос от лица пользователя, записывает запрос в БД.
- GET /requests - возвращает список DTO всех запросов пользователя.
- GET /requests/all - возвращает список DTO всех запросов.
- GET /requests/{requestId} - возвращает DTO запроса по id.
- DELETE /requests/{requestId} - удаляет запрос по id.