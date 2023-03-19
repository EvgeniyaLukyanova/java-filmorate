package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public UserService(UserStorage userStorage) {
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
        if (userStorage.getUserById(id) != null && userStorage.getUserById(friendId) != null) {
            User user = userStorage.getUserById(id);
            User friendUser = userStorage.getUserById(friendId);

            if (user.getFriends() == null) {
                Set<Integer> setFriend = new HashSet<>();
                user.setFriends(setFriend);
            }
            user.getFriends().add(friendId);

            if (friendUser.getFriends() == null) {
                Set<Integer> setFriend = new HashSet<>();
                friendUser.setFriends(setFriend);
            }
            friendUser.getFriends().add(id);
        } else {
            if (userStorage.getUserById(id) == null) {
                log.warn("Добавление друга пользователю с ид {}. Пользователь отсутствует в базе.", id);
                throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
            }
            if (userStorage.getUserById(friendId) == null) {
                log.warn("Добавление друга пользователю с ид {}. Пользователь с ид {} отсутствует в базе.", id, friendId);
                throw new NotFoundException(String.format("Пользователь с ид %s не найден", friendId));
            }
        }
    }

    public void deleteFriend(int id, int friendId) {
        if (userStorage.getUserById(id) != null && userStorage.getUserById(friendId) != null) {
            User user = userStorage.getUserById(id);
            User friendUser = userStorage.getUserById(friendId);

            if (user.getFriends() != null) {
                user.getFriends().remove(friendId);
            }

            if (friendUser.getFriends() != null) {
                friendUser.getFriends().remove(id);
            }
        } else {
            if (userStorage.getUserById(id) == null) {
                log.warn("Удаление друга у пользователя с ид {}. Пользователь отсутствует в базе.", id);
                throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
            }
            if (userStorage.getUserById(friendId) == null) {
                log.warn("Удаление друга у пользователя с ид {}. Пользователь с ид {} отсутствует в базе.", id, friendId);
                throw new NotFoundException(String.format("Пользователь с ид %s не найден", friendId));
            }
        }
    }

    public List<User> getFriends(int id) {
        if (userStorage.getUserById(id) != null) {
            User user = userStorage.getUserById(id);
            if (user.getFriends() != null) {
                return user.getFriends().stream()
                        .map(e -> userStorage.getUserById(e))
                        .collect(Collectors.toList());
            }
        } else {
            log.warn("Получение списка друзей. Пользователь с ид {} отсутствует в базе.", id);
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }
        return new ArrayList<>();
    }

    public List<User> getCommonFriends(int id, int otherId) {
        if (userStorage.getUserById(id) != null && userStorage.getUserById(otherId) != null) {
            User user = userStorage.getUserById(id);
            User otherUser = userStorage.getUserById(otherId);

            if (user.getFriends() != null && otherUser.getFriends() != null) {
                return user.getFriends().stream()
                        .filter(t -> otherUser.getFriends().contains(t))
                        .map(e -> userStorage.getUserById(e))
                        .collect(Collectors.toList());
            }
        } else {
            if (userStorage.getUserById(id) == null) {
                log.warn("Получение списка общих друзей. Пользователь с ид {} отсутствует в базе.", id);
                throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
            }
            if (userStorage.getUserById(otherId) == null) {
                log.warn("Получение списка общих друзей. Пользователь с ид {} отсутствует в базе.", otherId);
                throw new NotFoundException(String.format("Пользователь с ид %s не найден", otherId));
            }
        }
        return new ArrayList<>();
    }
}
