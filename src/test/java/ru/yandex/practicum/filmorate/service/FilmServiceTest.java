package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
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

class FilmServiceTest {
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private FilmService filmService;
    private static Validator validator;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void validateTest() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    public void blankNameTest() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Название фильма не может быть пустым.");
    }

    @Test
    public void emptyNameTest() {
        Film film = new Film();
        film.setName("");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Название фильма не может быть пустым.");
    }

    @Test
    public void nullNameTest() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Название фильма не может быть пустым.");
    }

    @Test
    public void maxLengthMore200Test() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
        film.setReleaseDate(LocalDate.of(1900, 3, 25));
        film.setDuration(200);
        film.setMpa(new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Максимальная длина описания не должна превышать 200 символов.");
    }

    @Test
    public void noCorrectReleaseDateTest() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1890, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Дата релиза должны быть не раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void durationMore0Test() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(-200);
        film.setMpa(new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Продолжительность фильма должна быть положительной.");
    }

    @Test
    void crateFilm() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        int filmId = film.getId();
        Film savedFilm = filmStorage.getFilmById(filmId);
        assertNotNull(savedFilm, "Фильм не найден.");
        assertEquals(film, savedFilm, "Фильмы не совпадают.");
        List<Film> films = filmStorage.getFilms();
        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, films.size(), "Неверное количество фтльмов.");
        assertEquals(film, films.get(0), "Фильмы не совпадают.");
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        film.setName("nisi eiusmod new");
        film.setDescription("adipisicing new");
        film.setReleaseDate(LocalDate.of(1970, 3, 25));
        film.setDuration(200);
        filmService.updateFilm(film);
        int filmId = film.getId();
        Film savedFilm = filmStorage.getFilmById(filmId);
        assertNotNull(savedFilm, "Фильм не найден.");
        assertEquals(film, savedFilm, "Фильмы не совпадают.");
        List<Film> films = filmStorage.getFilms();
        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        assertEquals("nisi eiusmod new", films.get(0).getName(), "Не изменилось наименование фильма.");
        assertEquals("adipisicing new", films.get(0).getDescription(), "Не изменилось опимание фильма.");
        assertEquals(200, films.get(0).getDuration(), "Не изменилась продолжительности фильма.");
        assertEquals(LocalDate.of(1970, 3, 25), films.get(0).getReleaseDate(), "Не изменилась дата релиза.");
    }

    @Test
    void getFilmById() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        int filmId = film.getId();
        Film savedFilm = filmService.getFilmById(filmId);
        assertNotNull(savedFilm, "Фильм не найден.");
        assertEquals(film, savedFilm, "Фильмы не совпадают.");
    }

    @Test
    void getFilms() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        List<Film> films = filmService.getFilms();
        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        assertEquals(film, films.get(0), "Неверный список фильмов");
    }

    @Test
    void addLike() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        userStorage.crateUser(user);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        filmService.addLike(film.getId(), user.getId());
        List<Film> films = filmService.getFilms();
        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        assertNotNull(films.get(0).getLikes(), "Нет лайков у фильма");
        assertEquals(1, films.get(0).getLikes().size(), "Неверное количество лайков.");
        assertTrue(films.get(0).getLikes().contains(user), "Лайк не того пользователя");
    }

    @Test
    void deleteLike() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        userStorage.crateUser(user);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        filmService.addLike(film.getId(), user.getId());
        filmService.deleteLike(film.getId(), user.getId());
        List<Film> films = filmService.getFilms();
        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        assertEquals(0, films.get(0).getLikes().size(), "Неверное количество лайков.");
    }

    @Test
    void getPopularFilms() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film);
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        userStorage.crateUser(user);
        Film film1 = new Film();
        film1.setName("nisi eiusmod");
        film1.setDescription("adipisicing");
        film1.setReleaseDate(LocalDate.of(1967, 3, 25));
        film1.setDuration(100);
        film1.setMpa(new Mpa(1, "G"));
        filmService.crateFilm(film1);
        filmService.addLike(film1.getId(), user.getId());
        List<Film> films = filmService.getPopularFilms(null);
        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(film1, films.get(0), "Фильмы не в том порядке.");
        assertEquals(film, films.get(1), "Фильмы не в том порядке.");
    }
}