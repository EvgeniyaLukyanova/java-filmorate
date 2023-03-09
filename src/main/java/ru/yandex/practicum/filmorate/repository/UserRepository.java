package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private int uniqueId = 0;
    public final Map<Integer, User> users = new HashMap<>();

    private int getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    public void crateUser(User user) {
        user.setId(getUniqueId());
        users.put(user.getId(), user);
    }

    public void updateUser(User user) {
        users.put(user.getId(), user);
    }
}
