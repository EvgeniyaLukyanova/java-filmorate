package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpaById(int id) {
        Mpa genre = mpaStorage.getMpaById(id);
        if (genre == null) {
            log.warn("Получение рейтинга по ид {}. Рейтинг отсутствует в базе.", id);
            throw new NotFoundException(String.format("Рейтинг с ид %s не найден", id));
        }
        return genre;
    }

    public List<Mpa> getMpa() {
        return mpaStorage.getMpa();
    }
}
