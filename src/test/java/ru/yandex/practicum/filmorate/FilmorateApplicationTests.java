package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;
	private final FilmDbStorage filmStorage;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@AfterEach
	void tearDown() {
		jdbcTemplate.execute("delete from \"friends\"");
		jdbcTemplate.execute("delete from \"likes\"");
		jdbcTemplate.execute("delete from \"users\"");
		jdbcTemplate.execute("delete from \"film_genres\"");
		jdbcTemplate.execute("delete from \"films\"");
		jdbcTemplate.execute("ALTER TABLE \"users\" ALTER COLUMN \"id\" RESTART WITH 1");
		jdbcTemplate.execute("ALTER TABLE \"films\" ALTER COLUMN \"id\" RESTART WITH 1");
	}

	@Test
	public void testCreateAndFindUserById() {
		User user = new User();
		user.setEmail("mail@mail.ru");
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user);

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
		assertThat(userOptional).isPresent().hasValueSatisfying(u -> assertAll(
				() -> assertThat(u).hasFieldOrPropertyWithValue("id", 1),
				() -> assertThat(u).hasFieldOrPropertyWithValue("email", "mail@mail.ru"),
				() -> assertThat(u).hasFieldOrPropertyWithValue("login", "dolore"),
				() -> assertThat(u).hasFieldOrPropertyWithValue("name", "Nick Name"),
				() -> assertThat(u).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1946,8,20))
				));
	}

	@Test
	public void testUpdateUser() {
		User user = new User();
		user.setEmail("mail@mail.ru");
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user);

		user.setEmail("mailNew@mail.ru");
		user.setLogin("doloreNew");
		user.setName("Nick Name New");
		user.setBirthday(LocalDate.of(1950,8,20));
		userStorage.updateUser(user);

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
		assertThat(userOptional).isPresent().hasValueSatisfying(u -> assertAll(
				() -> assertThat(u).hasFieldOrPropertyWithValue("id", 1),
				() -> assertThat(u).hasFieldOrPropertyWithValue("email", "mailNew@mail.ru"),
				() -> assertThat(u).hasFieldOrPropertyWithValue("login", "doloreNew"),
				() -> assertThat(u).hasFieldOrPropertyWithValue("name", "Nick Name New"),
				() -> assertThat(u).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1950,8,20))
		));
	}

	@Test
	public void testUpdateUserAddFriend() {
		User user1 = new User();
		user1.setEmail("mail@mail.ru");
		user1.setLogin("dolore");
		user1.setName("Nick Name");
		user1.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user1);

		User user2 = new User();
		user2.setEmail("mail@mail.ru");
		user2.setLogin("friend");
		user2.setName("Nick Name");
		user2.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user2);

		List<Integer> friends = new ArrayList<>();
		friends.add(user2.getId());
		user1.setFriends(friends);
		userStorage.updateUser(user1);

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
		assertThat(userOptional).isPresent().hasValueSatisfying(u ->
				assertThat(u).hasFieldOrPropertyWithValue("friends", friends)
				);
	}

	@Test
	public void testUpdateUserDeleteFriend() {
		User user1 = new User();
		user1.setEmail("mail@mail.ru");
		user1.setLogin("dolore");
		user1.setName("Nick Name");
		user1.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user1);

		User user2 = new User();
		user2.setEmail("mail@mail.ru");
		user2.setLogin("friend");
		user2.setName("Nick Name");
		user2.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user2);

		List<Integer> friends = new ArrayList<>();
		friends.add(user2.getId());
		user1.setFriends(friends);
		userStorage.updateUser(user1);

		user1.getFriends().remove((Integer)user2.getId());
		userStorage.updateUser(user1);

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional).isPresent().hasValueSatisfying(u ->
				assertThat(u).hasFieldOrPropertyWithValue("friends", user1.getFriends())
		);
	}

	@Test
	public void testGetUsers() {
		User user = new User();
		user.setEmail("mail@mail.ru");
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user);

		List<User> users = userStorage.getUsers();

		assertThat(users).isEqualTo(List.of(user));
	}

	@Test
	public void testGetGenres() {
		assertThat(genreStorage.getGenre()).hasSize(6)
				.extracting(Genre::getName)
				.containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
	}

	@Test
	public void testFindGenreById() {
		Optional<Genre> genre = Optional.ofNullable(genreStorage.getGenreById(1));
		assertThat(genre).isPresent()
				.hasValueSatisfying(g -> assertThat(g.getName()).isIn("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"));
	}

	@Test
	public void testGetMpa() {
		assertThat(mpaStorage.getMpa()).hasSize(5)
				.extracting(Mpa::getName)
				.containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
	}

	@Test
	public void testFindMpaById() {
		Optional<Mpa> mpa = Optional.ofNullable(mpaStorage.getMpaById(1));
		assertThat(mpa).isPresent()
				.hasValueSatisfying(m -> assertThat(m.getName()).isIn("G", "PG", "PG-13", "R", "NC-17"));
	}

	@Test
	public void testCreateAndFindFilmById() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,3,25));
		film.setDuration(100);
		filmStorage.crateFilm(film);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
		assertThat(filmOptional).isPresent().hasValueSatisfying(f -> assertAll(
				() -> assertThat(f).hasFieldOrPropertyWithValue("id", 1),
				() -> assertThat(f).hasFieldOrPropertyWithValue("name", "nisi eiusmod"),
				() -> assertThat(f).hasFieldOrPropertyWithValue("description", "adipisicing"),
				() -> assertThat(f).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967,3,25)),
				() -> assertThat(f).hasFieldOrPropertyWithValue("duration", 100)
		));
	}

	@Test
	public void testUpdateFilm() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,3,25));
		film.setDuration(100);
		filmStorage.crateFilm(film);

		film.setName("nisi eiusmod new");
		film.setDescription("adipisicing new");
		film.setReleaseDate(LocalDate.of(1970,3,25));
		film.setDuration(200);
		filmStorage.updateFilm(film);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
		assertThat(filmOptional).isPresent().hasValueSatisfying(f -> assertAll(
				() -> assertThat(f).hasFieldOrPropertyWithValue("id", 1),
				() -> assertThat(f).hasFieldOrPropertyWithValue("name", "nisi eiusmod new"),
				() -> assertThat(f).hasFieldOrPropertyWithValue("description", "adipisicing new"),
				() -> assertThat(f).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1970,3,25)),
				() -> assertThat(f).hasFieldOrPropertyWithValue("duration", 200)
		));
	}

	@Test
	public void testUpdateFilmAddLike() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,3,25));
		film.setDuration(100);
		filmStorage.crateFilm(film);

		User user = new User();
		user.setEmail("mail@mail.ru");
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user);

		Set<Integer> setLikes = new HashSet<>();
		setLikes.add(user.getId());
		film.setLikes(setLikes);
		filmStorage.updateFilm(film);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
		assertThat(filmOptional).isPresent().hasValueSatisfying(f ->
				assertThat(f).hasFieldOrPropertyWithValue("likes", setLikes)
		);
	}

	@Test
	public void testUpdateFilmDeleteLike() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,3,25));
		film.setDuration(100);
		filmStorage.crateFilm(film);

		User user = new User();
		user.setEmail("mail@mail.ru");
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setBirthday(LocalDate.of(1946,8,20));
		userStorage.crateUser(user);

		Set<Integer> setLikes = new HashSet<>();
		setLikes.add(user.getId());
		film.setLikes(setLikes);
		filmStorage.updateFilm(film);

		film.getLikes().remove(user.getId());
		filmStorage.updateFilm(film);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
		assertThat(filmOptional).isPresent().hasValueSatisfying(f ->
				assertThat(f).hasFieldOrPropertyWithValue("likes", film.getLikes())
		);
	}

	@Test
	public void testUpdateFilmMpa() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,3,25));
		film.setDuration(100);
		filmStorage.crateFilm(film);

		Mpa mpa = mpaStorage.getMpaById(1);
		film.setMpa(mpa);
		filmStorage.updateFilm(film);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
		assertThat(filmOptional).isPresent().hasValueSatisfying(f ->
				assertThat(f).hasFieldOrPropertyWithValue("mpa", mpa)
		);
	}

	@Test
	public void testUpdateFilmGenres() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,3,25));
		film.setDuration(100);
		filmStorage.crateFilm(film);

		List<Genre> genres = new ArrayList<>();
		genres.add(genreStorage.getGenreById(1));
		film.setGenres(genres);
		filmStorage.updateFilm(film);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
		assertThat(filmOptional).isPresent().hasValueSatisfying(f ->
				assertThat(f).hasFieldOrPropertyWithValue("genres", genres)
		);
	}
}
