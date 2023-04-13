package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserStorage userStorage;
    private UserService userService;
    private static Validator validator;

    @BeforeEach
    public void beforeEach(){
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

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
        userService.validate(user);
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
        userService.validate(user);
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
    void crateUser() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user);
        int userId = user.getId();
        User savedUser = userStorage.getUserById(userId);
        assertNotNull(savedUser, "Пользователь не найден.");
        assertEquals(user, savedUser, "Пользователи не совпадают.");
        List<User> users = userStorage.getUsers();
        assertNotNull(users, "Пользователи не возвращаются.");
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        assertEquals(user, users.get(0), "Пользователи не совпадают.");
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user);
        user.setEmail("mailNew@mail.ru");
        user.setLogin("doloreNew");
        user.setName("Nick Name New");
        user.setBirthday(LocalDate.of(1950,8,20));
        userService.updateUser(user);
        int userId = user.getId();
        User savedUser = userStorage.getUserById(userId);
        assertNotNull(savedUser, "Пользователь не найден.");
        assertEquals(user, savedUser, "Пользователи не совпадают.");
        List<User> users = userStorage.getUsers();
        assertNotNull(users, "Пользователи не возвращаются.");
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        assertEquals("Nick Name New", users.get(0).getName(), "Не изменилось имя пользователя.");
        assertEquals("doloreNew", users.get(0).getLogin(), "Не изменился логин пользователя.");
        assertEquals("mailNew@mail.ru", users.get(0).getEmail(), "Не изменилась почта пользователя.");
        assertEquals(LocalDate.of(1950,8,20), users.get(0).getBirthday(), "Не изменилась дата рождения пользователя.");
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user);
        int userId = user.getId();
        User savedUser = userService.getUserById(userId);
        assertNotNull(savedUser, "Пользователь не найден.");
        assertEquals(user, savedUser, "Пользователи не совпадают.");
    }

    @Test
    void getUsers() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user);
        List<User> users = userService.getUsers();
        assertNotNull(users, "Пользователи не возвращаются.");
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        assertEquals(user.toString(), users.get(0).toString(), "Неверный список пользователей");
    }

    @Test
    void addFriend() {
        User user1 = new User();
        user1.setEmail("mail@mail.ru");
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user1);
        User user2 = new User();
        user2.setEmail("mail@mail.ru");
        user2.setLogin("friend");
        user2.setName("Nick Name");
        user2.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user2);
        userService.addFriend(user1.getId(), user2.getId());
        List<User> users = userService.getUsers();
        assertNotNull(users, "Пользователи не возвращаются.");
        assertEquals(2, users.size(), "Неверное количество пользователей.");
        assertNotNull(users.get(0).getFriends(), "Нет друзей у пользователя");
        assertEquals(1, users.get(0).getFriends().size(), "Неверное количество друзей.");
        assertTrue(users.get(0).getFriends().contains(users.get(1).getId()), "Неверный друг у пользователя");
    }

    @Test
    void deleteFriend() {
        User user1 = new User();
        user1.setEmail("mail@mail.ru");
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user1);
        User user2 = new User();
        user2.setEmail("mail@mail.ru");
        user2.setLogin("friend");
        user2.setName("Nick Name");
        user2.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user2);
        userService.addFriend(user1.getId(), user2.getId());
        userService.deleteFriend(user1.getId(), user2.getId());
        List<User> users = userService.getUsers();
        assertNotNull(users, "Пользователи не возвращаются.");
        assertEquals(2, users.size(), "Неверное количество пользователей.");
        assertEquals(0, users.get(0).getFriends().size(), "Неверное количество друзей.");
        assertEquals(0, users.get(1).getFriends().size(), "Неверное количество друзей.");
    }

    @Test
    void getFriends() {
        User user1 = new User();
        user1.setEmail("mail@mail.ru");
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user1);
        User user2 = new User();
        user2.setEmail("mail@mail.ru");
        user2.setLogin("friend");
        user2.setName("Nick Name");
        user2.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user2);
        userService.addFriend(user1.getId(), user2.getId());
        List<User> users = userService.getFriends(user1.getId());
        assertNotNull(users, "Нет друзей у пользователя.");
        assertEquals(1, users.size(), "Неверное количество друзей.");
        assertEquals(users.get(0), user2, "Неверный друг у пользователя");
    }

    @Test
    void getCommonFriends() {
        User user1 = new User();
        user1.setEmail("mail@mail.ru");
        user1.setLogin("friend1");
        user1.setName("Nick Name");
        user1.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user1);
        User user2 = new User();
        user2.setEmail("mail@mail.ru");
        user2.setLogin("friend2");
        user2.setName("Nick Name");
        user2.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user2);
        User user3 = new User();
        user3.setEmail("mail@mail.ru");
        user3.setLogin("friend3");
        user3.setName("Nick Name");
        user3.setBirthday(LocalDate.of(1946,8,20));
        userService.crateUser(user3);
        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());
        List<User> users = userService.getCommonFriends(user1.getId(), user2.getId());
        assertNotNull(users, "Нет общих друзей.");
        assertEquals(1, users.size(), "Неверное количество друзей.");
        assertEquals(users.get(0), user3, "Неверный друг");
    }
}