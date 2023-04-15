package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Like mapRowToLike(ResultSet resultSet) throws SQLException {
        Integer filmId = resultSet.getInt("film_id");
        Integer userId = resultSet.getInt("user_id");

        return new Like(filmId, userId);
    }

    @Override
    public void createLike(Like like) {
        String sqlQuery = "insert into \"likes\" (\"film_id\", \"user_id\") values (?,?)";
        jdbcTemplate.update(sqlQuery, like.getFilmId(), like.getUserId());
    }

    @Override
    public void deleteLike(Like like) {
        String sqlQuery = "delete from \"likes\" where \"film_id\" = ? and \"user_id\" = ?";
        jdbcTemplate.update(sqlQuery, like.getFilmId(), like.getUserId());
    }

    @Override
    public Set<Like> getLikeByFilmId(int filmId) {
        String sqlQuery = "select * from \"likes\" where \"film_id\" = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToLike(resultSet), filmId));
    }
}
