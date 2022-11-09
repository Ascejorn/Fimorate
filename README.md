# Filmorate project.
ER-диаграмма
![](src/main/resources/db_scheme.png)


# Описание
.....
## Features

1. Получение всех фильмов
```bash
SELECT * FROM films;
```
2. Получение жанров фильма по его id
```bash
SELECT g.genre
FROM films_genres f
JOIN genres g
    ON g.genre_id = f.genre_id
WHERE f.film_id = 1;
```
3. Получение всех пользователей
```bash
SELECT * FROM users;
```
4. Получение пользователя по id
```bash
SELECT *
FROM users u
WHERE u.user_id = 2;
```
5. Получение друзей пользователя по id
```bash
SELECT u.*
FROM friends f
JOIN users u
    ON f.friend_id = u.user_id
WHERE f.user_id = 1
    AND status = 'true';
```