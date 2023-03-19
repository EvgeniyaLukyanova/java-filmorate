package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
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
    private Set<Integer> friends;
}
