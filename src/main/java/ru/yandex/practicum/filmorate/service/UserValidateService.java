package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import java.time.LocalDate;

@Component
@Slf4j
public class UserValidateService {
    final UserRepository repository;

    public UserValidateService(UserRepository repository) {
        this.repository = repository;
    }

    public void validate(User user) {
        if (user.getId() != 0) {
            if (!repository.users.containsKey(user.getId())) {
                log.warn("Пользователь отсутствует в базе: {}", user);
                throw new ValidationException("Пользователь с ид " + user.getId() + " отсутствует в базе.");
            }
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Адрес электронной почты не может быть пустым: {}", user);
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Неверный формат адреса электронной почты: {}", user);
            throw new ValidationException("Неверный формат адреса электронной почты.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Логин не может быть пустым: {}", user);
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Логин содержит пробелы: {}", user);
            throw new ValidationException("Логин содержит пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем: {}", user);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
