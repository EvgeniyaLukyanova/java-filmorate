package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
public class User {
    @NotNull
    private int id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    private List<Integer> friends = new ArrayList<>();

    public User(){}

    public User(int id, String login, String name, String email, LocalDate birthday, List<Integer> friends) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.friends = friends;
    }
}
