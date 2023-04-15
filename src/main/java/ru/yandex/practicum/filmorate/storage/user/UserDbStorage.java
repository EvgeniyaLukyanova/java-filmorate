package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    FriendStorage friendStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();

        Set<User> friends = friendStorage.getFriendByUserId(id).stream()
                .map(e -> getUserById(e.getUser2Id()))
                .collect(Collectors.toSet());
        return new User(id, email, login, name, birthday, friends);
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

            Set<User> friends = friendStorage.getFriendByUserId(user.getId()).stream()
                    .map(e -> getUserById(e.getUser2Id()))
                    .collect(Collectors.toSet());

            if (user.getFriends() != null) {
                for (User friend : user.getFriends()) {
                    if (friends != null) {
                        if (!friends.contains(friend)) {
                            friendStorage.createFriend(new Friend(user.getId(), friend.getId(), true));
                        }
                    } else {
                        friendStorage.createFriend(new Friend(user.getId(), friend.getId(), true));
                    }
                }
            }

            for (User friend : friends) {
                if (user.getFriends() != null) {
                    if (!user.getFriends().contains(friend)) {
                        friendStorage.deleteFriend(new Friend(user.getId(), friend.getId(), true));
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
}
