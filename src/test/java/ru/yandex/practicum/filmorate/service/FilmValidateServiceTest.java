package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidateServiceTest {

    private FilmRepository repository;
    private FilmValidateService validateService;
    private static Validator validator;

    @BeforeEach
    public void beforeEach(){
        repository = new FilmRepository();
        validateService = new FilmValidateService(repository);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void validateTest() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        validateService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    public void blankNameTest() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        validateService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Название фильма не может быть пустым.");
    }

    @Test
    public void emptyNameTest() {
        Film film = new Film();
        film.setName("");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        validateService.validate(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Название фильма не может быть пустым.");
    }

    @Test
    public void nullNameTest() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        validateService.validate(film);
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
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Максимальная длина описания не должна превышать 200 символов.");
    }

    @Test
    public void noCorrectReleaseDateTest() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1890,3,25));
        film.setDuration(100);
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(film));
        assertEquals("Дата релиза должны быть не раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void durationMore0Test() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(-200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Продолжительность фильма должна быть положительной.");
    }

    @Test
    public void invalidIdTest() {
        Film film = new Film();
        film.setId(9999);
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        ValidationException exception = assertThrows(ValidationException.class, () -> validateService.validate(film));
        assertEquals("Фильм \"nisi eiusmod\" отсутствует в базе.", exception.getMessage());
    }
}