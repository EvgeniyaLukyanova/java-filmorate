package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        List<Integer> friends = getFriends(id);
        return new User(id, login, name, email, birthday, friends);
    }

    @Override
    public void crateUser(User user) {
        if (user != null) {
            String sqlQuery = "insert into \"users\"(\"login\", \"name\", \"email\", \"birthday\") " +
                    "values (?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getEmail());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
            user.setId(keyHolder.getKey().intValue());
        }
    }

    @Override
    public void updateUser(User user) {
        if (user != null) {
            String sqlQuery = "update \"users\" set " +
                    "\"login\" = ?, \"name\" = ?, \"email\" = ?, \"birthday\" = ? " +
                    "where \"id\" = ?";
            jdbcTemplate.update(sqlQuery, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());

            List<Integer> friends = getFriends(user.getId());

            if (user.getFriends() != null) {
                for (Integer id : user.getFriends()) {
                    if (friends != null) {
                        if (!friends.contains(id)) {
                            addFriend(user.getId(), id);
                        }
                    } else {
                        addFriend(user.getId(), id);
                    }
                }
            }

            for (Integer id : friends) {
                if (user.getFriends() != null) {
                    if (!user.getFriends().contains(id)) {
                        deleteFriend(user.getId(), id);
                    }
                }
            }
        }
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "select * from \"users\" where \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> mapRowToUser(resultSet), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "select * from \"users\"";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToUser(resultSet));
    }

    public void addFriend(int id, int friendId) {
        String sqlQuery = "insert into \"friends\"(\"user1_id\", \"user2_id\", \"confirmation\") " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                id,
                friendId,
                true);
    }

    public void deleteFriend(int id, int friendId) {
        String sqlQuery = "delete from \"friends\" where \"user1_id\" = ? and \"user2_id\" = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    public List<Integer> getFriends(int id) {
        String sqlQuery = "select \"user2_id\" from \"friends\" where \"user1_id\" = ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            return resultSet.getInt("user2_id");
            }, id);
    }
}
