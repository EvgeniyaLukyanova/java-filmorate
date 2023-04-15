package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage")FilmStorage filmStorage, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void validate(Film film) {
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("Дата релиза должны быть не раньше 28 декабря 1895 года: {}", film);
                throw new ValidationException("Дата релиза должны быть не раньше 28 декабря 1895 года.");
            }
        }
    }

    public void crateFilm(Film film) {
        filmStorage.crateFilm(film);
    }


    public void updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            log.warn("Изменение фильма. Фильм отсутствует в базе: {}", film);
            throw new NotFoundException(String.format("Фильм с ид %s не найден", film.getId()));
        }
        filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Получение фильма по ид {}. Фильм отсутствует в базе.", id);
            throw new NotFoundException(String.format("Фильм с ид %s не найден", id));
        }
        return film;
    }


    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Добавление лайка фильму с ид {}. Фильм отсутствует в базе.", id);
            throw new NotFoundException(String.format("Фильм с ид %s не найден", id));
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Добавление лайка фильму с ид {}. Пользователь с ид {} не найден", id, userId);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }

        if (film.getLikes() == null) {
            Set<User> setLikes = new HashSet<>();
            film.setLikes(setLikes);
        }

        if (film.getLikes().add(user)) {
            filmStorage.updateFilm(film);
        }
    }

    public void deleteLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Удаление лайка у фильма с ид {}. Фильм отсутствует в базе.", id);
            throw new NotFoundException(String.format("Фильм с ид %s не найден", id));
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Удвлкние лайка у фильма с ид {}. Пользователь с ид {} отсутствует в базе", id, userId);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }

        if (film.getLikes() != null) {
            if (film.getLikes().remove(user)) {
                filmStorage.updateFilm(film);
            }
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        int cnt = Optional.ofNullable(count).orElse(10);
        if (cnt > 0) {
            return filmStorage.getFilms().stream()
                    .sorted((Film o1, Film o2) -> {
                        if (o1.getLikes() == null) {
                            return 1;
                        }
                        if (o2.getLikes() == null) {
                            return -1;
                        }
                        if (o1.getLikes().size() > o2.getLikes().size()) {
                            return -1;
                        } else {
                            return 1;
                        }
                        })
                    .limit(cnt)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}


