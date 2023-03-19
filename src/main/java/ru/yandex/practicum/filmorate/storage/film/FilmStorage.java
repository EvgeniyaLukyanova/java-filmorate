package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    void crateFilm(Film film);

    void updateFilm(Film film);

    Film getFilmById(int id);

    List<Film> getFilms();
}
