package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.HashMap;
import java.util.Map;

@Component
public class FilmRepository {
    private int uniqueId = 0;
    public final Map<Integer, Film> films = new HashMap<>();

    private int getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    public void crateFilm(Film film) {
        film.setId(getUniqueId());
        films.put(film.getId(), film);
    }

    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }
}
