package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Начинаем добавлять фильм: {}", film);
        filmService.validate(film);
        filmService.crateFilm(film);
        log.info("Фильм добавлен: {}", film);
        return filmService.getFilmById(film.getId());
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.info("Начинаем обновлять фильм: {}", film);
        filmService.validate(film);
        filmService.updateFilm(film);
        log.info("Фильм обнавлен: {}", film);
        return filmService.getFilmById(film.getId());
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение списка всех фильмов: {}", filmService.getFilms());
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Получение фильма с ид {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Начинаем ставить лайк фильму с ид {} пользователем с ид {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Добавлен лайк фильму с ид {} пользователем с ид {}: {}", id, userId, filmService.getFilmById(id));
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Начинаем удалять лайк у фильма с ид {} пользователя с ид {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Удален лайк фильма с ид {} пользователя с ид {}: {}", id, userId, filmService.getFilmById(id));
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Получение списка первых {} фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
