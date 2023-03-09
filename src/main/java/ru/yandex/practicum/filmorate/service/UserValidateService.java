package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
