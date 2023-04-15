package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Set;

public interface LikeStorage {
    void createLike(Like like);

    void deleteLike(Like like);

    Set<Like> getLikeByFilmId(int filmId);
}
