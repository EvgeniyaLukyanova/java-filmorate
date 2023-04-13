package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(int id) {
        Genre genre = genreStorage.getGenreById(id);
        if (genre == null) {
            log.warn("Получение жанра по ид {}. Жанр отсутствует в базе.", id);
            throw new NotFoundException(String.format("Жанр с ид %s не найден", id));
        }
        return genre;
    }

    public List<Genre> getGenre() {
        return genreStorage.getGenre();
    }
}
