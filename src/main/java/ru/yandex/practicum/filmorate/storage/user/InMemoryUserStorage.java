package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int uniqueId = 0;
    public final Map<Integer, User> users = new HashMap<>();

    private int getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public void crateUser(User user) {
        if (user != null) {
            user.setId(getUniqueId());
            users.put(user.getId(), user);
        }
    }

    @Override
    public void updateUser(User user) {
        if (user != null) {
            users.put(user.getId(), user);
        }
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
