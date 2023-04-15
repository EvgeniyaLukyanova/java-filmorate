package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    MpaStorage mpaStorage;

    @Autowired
    GenreStorage genreStorage;

    @Autowired
    LikeStorage likeStorage;

    @Autowired
    @Qualifier("UserDbStorage")
    UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film mapRowToFilm(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate release = resultSet.getDate("release").toLocalDate();
        int duration = resultSet.getInt("duration");
        int rate = resultSet.getInt("rate");

        Set<User> likes = likeStorage.getLikeByFilmId(id).stream()
                .map(e -> userStorage.getUserById(e.getUserId()))
                .collect(Collectors.toSet());

        Mpa mpa = mpaStorage.getMpaById(resultSet.getInt("mpa_id"));

        Set<Genre> genres = getGenresByIdFilm(id);

        return new Film(id, name, description, release, duration, rate, likes, mpa, genres);
    }

    @Override
    public void crateFilm(Film film) {
        if (film != null) {
            String sqlQuery = "insert into \"films\"(\"name\", \"description\", \"release\", \"duration\", \"rate\", \"mpa_id\") " +
                    "values(?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getRate());
                if (film.getMpa() != null) {
                    stmt.setInt(6, film.getMpa().getId());
                } else {
                    stmt.setNull(6, Types.INTEGER);
                }
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().intValue());

            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    addGenre(film.getId(), genre.getId());
                }
            }
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (film != null) {
            String sqlQuery = "update \"films\" " +
                    "set \"name\" = ?, " +
                    "    \"release\" = ?, " +
                    "    \"description\" = ?, " +
                    "    \"duration\" = ?, " +
                    "    \"rate\" = ?, " +
                    "    \"mpa_id\" = ? " +
                    "where \"id\" = ?";
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                        stmt.setString(1, film.getName());
                        stmt.setDate(2, Date.valueOf(film.getReleaseDate()));
                        stmt.setString(3, film.getDescription());
                        stmt.setInt(4, film.getDuration());
                        stmt.setInt(5, film.getRate());
                        if (film.getMpa() != null) {
                            stmt.setInt(6, film.getMpa().getId());
                        } else {
                            stmt.setNull(6, Types.INTEGER);
                        }
                        stmt.setInt(7, film.getId());
                        return stmt;
                    });

            Set<User> likes = likeStorage.getLikeByFilmId(film.getId()).stream()
                    .map(e -> userStorage.getUserById(e.getUserId()))
                    .collect(Collectors.toSet());

            if (film.getLikes() != null) {
                for (User user : film.getLikes()) {
                    if (likes != null) {
                        if (!likes.contains(user)) {
                            likeStorage.createLike(new Like(film.getId(), user.getId()));
                        }
                    } else {
                        likeStorage.createLike(new Like(film.getId(), user.getId()));
                    }
                }
            }

            for (User user : likes) {
                if (film.getLikes() != null) {
                    if (!film.getLikes().contains(user)) {
                        likeStorage.deleteLike(new Like(film.getId(), user.getId()));
                    }
                }
            }

            List<Integer> genres = getGenres(film.getId());

            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    if (genres != null) {
                        if (!genres.contains(genre.getId())) {
                            addGenre(film.getId(), genre.getId());
                        }
                    } else {
                        addGenre(film.getId(), genre.getId());
                    }
                }
            }

            for (Integer id : genres) {
                if (film.getGenres() != null) {
                    if (!film.getGenres().stream()
                            .map(e -> e.getId())
                            .collect(Collectors.toList())
                            .contains(id)) {
                        deleteGenre(film.getId(), id);
                    }
                }
            }
        }
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "select * from \"films\" where \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> mapRowToFilm(resultSet), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "select * from \"films\"";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToFilm(resultSet));
    }

    public void addGenre(int filmId, int genreId) {
        String sqlQuery = "insert into \"film_genres\" (\"film_id\", \"genre_id\") values (?,?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public void deleteGenre(int filmId, int genreId) {
        String sqlQuery = "delete from \"film_genres\" where \"film_id\" =? and \"genre_id\" = ?";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public List<Integer> getGenres(int id) {
        String sqlQuery = "select \"genre_id\" from \"film_genres\" where \"film_id\" = ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            return resultSet.getInt("genre_id");
        }, id);
    }

    private Genre mapRowToGenre(ResultSet resultSet) throws SQLException {
        return genreStorage.getGenreById(resultSet.getInt("genre_id"));
    }

    public Set<Genre> getGenresByIdFilm(int id) {
        String sqlQuery = "select * from \"film_genres\" where \"film_id\" = ? order by \"id\"";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToGenre(resultSet), id).stream().collect(Collectors.toSet());
    }
}
