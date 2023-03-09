package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Component
class UserValidateServiceTest {

    private UserRepository repository;
    private UserValidateService validateService;
    private static Validator validator;

    @BeforeEach
    public void beforeEach(){
        repository = new UserRepository();
        validateService = new UserValidateService(repository);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void validateTest() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        validateService.validate(user);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void emptyEmailTest() {
        User user = new User();
        user.setEmail("");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Адрес электронной почты не может быть пустым.");
    }

    @Test
    public void nullEmailTest() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Адрес электронной почты не может быть пустым.");
    }

    @Test
    public void noCorrectEmailTest() {
        User user = new User();
        user.setEmail("mailmail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Неверный формат адреса электронной почты.");
    }

    @Test
    public void blankLoginTest() {
        User user = new User();
        user.setLogin(" ");
        user.setEmail("mail@mail.ru");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Логин не может быть пустым.");
    }

    @Test
    public void emptyLoginTest() {
        User user = new User();
        user.setLogin("");
        user.setEmail("mail@mail.ru");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Логин не может быть пустым.");
    }

    @Test
    public void loginWithSpaceTest() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore ullamco");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Логин не может содержать пробелы.");
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
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Дата рождения не может быть в будущем.");
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