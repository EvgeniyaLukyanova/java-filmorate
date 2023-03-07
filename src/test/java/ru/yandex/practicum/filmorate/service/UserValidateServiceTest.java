package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Component
class UserValidateServiceTest {

    UserRepository repository;
    UserValidateService validateService;

    @BeforeEach
    public void beforeEach(){
        repository = new UserRepository();
        validateService = new UserValidateService(repository);
    }

    @Test
    public void validateTest() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        validateService.validate(user);
    }

    @Test
    public void emptyEmailTest() {
        User user = new User();
        user.setEmail("");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(user));
        assertEquals("Адрес электронной почты не может быть пустым.", exception.getMessage());
    }

    @Test
    public void invalidEmailTest() {
        User user = new User();
        user.setEmail("mailmail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(user));
        assertEquals("Неверный формат адреса электронной почты.", exception.getMessage());
    }

    @Test
    public void emptyLoginTest() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(user));
        assertEquals("Логин не может быть пустым.", exception.getMessage());
    }
    @Test
    public void loginWithSpaceTest() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore ullamco");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(user));
        assertEquals("Логин содержит пробелы.", exception.getMessage());
    }

    @Test
    public void emptyNameTest() {
        User user = new User();
        user.setEmail("friend@common.ru");
        user.setLogin("common");
        user.setBirthday(LocalDate.of(2008,8,20));
        validateService.validate(user);
        assertEquals(user.getName(), "common", "Имя должно быть \"common\".");
    }

    @Test
    public void birthdayInFutureTest() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2446,8,20));
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(user));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    public void invalidIdTest() {
        User user = new User();
        user.setId(9999);
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(user));
        assertEquals("Пользователь с ид 9999 отсутствует в базе.", exception.getMessage());
    }
}