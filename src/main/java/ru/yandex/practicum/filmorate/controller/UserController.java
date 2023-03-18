package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.UserValidateService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserRepository repository;
    private final UserValidateService validateService;

    public UserController() {
        repository = new UserRepository();
        validateService = new UserValidateService(repository);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Начинаем добавлять пользователя: {}", user);
        validateService.validate(user);
        repository.crateUser(user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("Начинаем обновлять пользователя: {}", user);
        validateService.validate(user);
        repository.updateUser(user);
        log.info("Пользователь обнавлен: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение списка всех пользователей: {}", repository.users);
        return repository.users.values();
    }
}

