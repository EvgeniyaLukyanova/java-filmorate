package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Начинаем добавлять пользователя: {}", user);
        userService.validate(user);
        userService.crateUser(user);
        log.info("Пользователь добавлен: {}", user);
        return userService.getUserById(user.getId());
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("Начинаем обновлять пользователя: {}", user);
        userService.validate(user);
        userService.updateUser(user);
        log.info("Пользователь обнавлен: {}", user);
        return userService.getUserById(user.getId());
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение списка всех пользователей: {}", userService.getUsers());
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Получение пользователя с ид {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Начинаем добавление друга с ид {} пользователю с ид {}", friendId, id);
        userService.addFriend(id, friendId);
        log.info("Добавлен друг с ид {} пользователю с ид {}: {} {}", friendId, id, userService.getUserById(friendId), userService.getUserById(id));
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Начинаем удаление друга с ид {} у пользователя с ид {}", friendId, id);
        userService.deleteFriend(id, friendId);
        log.info("Удален друг с ид {} у пользователюъя с ид {}: {} {}", friendId, id, userService.getUserById(friendId), userService.getUserById(id));
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        log.info("Получение списка друзей пользователя с ид {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получение общего списка друзей пользователей с ид {}, {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}

