package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private Genre mapRowToGenre(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        return new Genre(id, name);
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "select * from \"genres\" where \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> mapRowToGenre(resultSet), id);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Genre> getGenre() {
        String sqlQuery = "select * from \"genres\"";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToGenre(resultSet));
    }
}
