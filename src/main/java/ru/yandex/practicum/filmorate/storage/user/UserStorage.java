package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void crateUser(User user);

    void updateUser(User user);

    User getUserById(int id);

    List<User> getUsers();
}
