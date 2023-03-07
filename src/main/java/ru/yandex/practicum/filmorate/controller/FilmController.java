package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.FilmValidateService;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmRepository repository;
    FilmValidateService validateService;

    public FilmController() {
        repository = new FilmRepository();
        validateService = new FilmValidateService(repository);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Начинаем добавлять фильм: {}", film);
        validateService.validate(film);
        repository.crateFilm(film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        log.info("Начинаем обновлять фильм: {}", film);
        validateService.validate(film);
        repository.updateFilm(film);
        log.info("Фильм обнавлен: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение списка всех фильмов: {}", repository.films);
        return repository.films.values();
    }
}
