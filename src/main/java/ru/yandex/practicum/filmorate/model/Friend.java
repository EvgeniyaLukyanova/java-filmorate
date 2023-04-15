package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friend {
    private int user1Id;
    private int user2Id;
    private boolean confirmation;
}
