package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Friend mapRowToFriend(ResultSet resultSet) throws SQLException {
        Integer user1Id = resultSet.getInt("user1_id");
        Integer user2Id = resultSet.getInt("user2_id");
        Boolean confirmation = resultSet.getBoolean("confirmation");

        return new Friend(user1Id, user2Id, confirmation);
    }

    @Override
    public void createFriend(Friend friend) {
        String sqlQuery = "insert into \"friends\"(\"user1_id\", \"user2_id\", \"confirmation\") " +
                "values (?, ?, ?)";

        jdbcTemplate.update(sqlQuery, friend.getUser1Id(), friend.getUser2Id(), friend.isConfirmation());
    }

    @Override
    public void deleteFriend(Friend friend) {
        String sqlQuery = "delete from \"friends\" where \"user1_id\" = ? and \"user2_id\" = ?";
        jdbcTemplate.update(sqlQuery, friend.getUser1Id(), friend.getUser2Id());
    }

    @Override
    public Set<Friend> getFriendByUserId(int userId) {
        String sqlQuery = "select * from \"friends\" where \"user1_id\" = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToFriend(resultSet), userId));
    }
}
