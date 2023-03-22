package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private int uniqueId = 0;
    public final Map<Integer, Film> films = new HashMap<>();

    private int getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public void crateFilm(Film film) {
        if (film != null) {
            film.setId(getUniqueId());
            films.put(film.getId(), film);
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (film != null) {
            films.put(film.getId(), film);
        }
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
