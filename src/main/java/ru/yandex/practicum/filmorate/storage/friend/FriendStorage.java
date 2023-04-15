package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.Set;

public interface FriendStorage {
    void createFriend(Friend friend);

    void deleteFriend(Friend friend);

    Set<Friend> getFriendByUserId(int userId);
}
