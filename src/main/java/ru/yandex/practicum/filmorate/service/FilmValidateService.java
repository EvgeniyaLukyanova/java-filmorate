package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidateService {
    final FilmRepository repository;

    public FilmValidateService(FilmRepository repository) {
        this.repository = repository;
    }

    public void validate(Film film) {
        if (film.getId() != 0) {
            if (!repository.films.containsKey(film.getId())) {
                log.warn("Фильм отсутствует в базе: {}", film);
                throw new ValidationException("Фильм \"" + film.getName() + "\" отсутствует в базе.");
            }
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым: {}", film);
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания не должна превышать 200 символов: {}", film);
            throw new ValidationException("Максимальная длина описания не должна превышать 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза должны быть не раньше 28 декабря 1895 года: {}", film);
            throw new ValidationException("Дата релиза должны быть не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма должна быть положительной: {}", film);
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
