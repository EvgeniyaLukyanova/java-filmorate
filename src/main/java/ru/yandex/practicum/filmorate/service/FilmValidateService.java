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
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("Дата релиза должны быть не раньше 28 декабря 1895 года: {}", film);
                throw new ValidationException("Дата релиза должны быть не раньше 28 декабря 1895 года.");
            }
        }
    }
}
