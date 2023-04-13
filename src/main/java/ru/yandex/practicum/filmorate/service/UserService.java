package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void crateUser(User user) {
        userStorage.crateUser(user);
    }

    public void updateUser(User user) {
        if (userStorage.getUserById(user.getId()) == null) {
            log.warn("Изменение пользователя. Пользователь отсутствует в базе: {}", user);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", user.getId()));
        }
        userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Получение пользователя по ид {}. Пользователь отсутствует в базе.", id);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Добавление друга пользователю с ид {}. Пользователь отсутствует в базе.", id);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }

        User friendUser = userStorage.getUserById(friendId);
        if (friendUser == null) {
            log.warn("Добавление друга пользователю с ид {}. Пользователь с ид {} отсутствует в базе.", id, friendId);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", friendId));
        }

        if (user.getFriends() != null) {
            if (!user.getFriends().contains(friendId)) {
                user.getFriends().add(friendId);
                userStorage.updateUser(user);
            }
        } else {
            List<Integer> friends = new ArrayList<>();
            friends.add(friendId);
            user.setFriends(friends);
            userStorage.updateUser(user);
        }
    }

    public void deleteFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Удаление друга у пользователя с ид {}. Пользователь отсутствует в базе.", id);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }

        User friendUser = userStorage.getUserById(friendId);
        if (friendUser == null) {
            log.warn("Удаление друга у пользователя с ид {}. Пользователь с ид {} отсутствует в базе.", id, friendId);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", friendId));
        }

        if (user.getFriends() != null) {
            if (user.getFriends().contains(friendId)) {
                user.getFriends().remove((Integer) friendId);
                userStorage.updateUser(user);
            }
        }
    }

    public List<User> getFriends(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Получение списка друзей. Пользователь с ид {} отсутствует в базе.", id);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }
        if (user.getFriends() != null) {
            return user.getFriends().stream()
                    .map(e -> userStorage.getUserById(e))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Получение списка общих друзей. Пользователь с ид {} отсутствует в базе.", id);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }

        User otherUser = userStorage.getUserById(otherId);
        if (otherUser == null) {
            log.warn("Получение списка общих друзей. Пользователь с ид {} отсутствует в базе.", otherId);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", otherId));
        }

        if (user.getFriends() != null && otherUser.getFriends() != null) {
            return user.getFriends().stream()
                    .filter(t -> otherUser.getFriends().contains(t))
                    .map(e -> userStorage.getUserById(e))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
