package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    @NotNull
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private int rate;
    private Set<Integer> likes;
    Mpa mpa;
    List<Genre> genres;

    public Film(){}

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, int rate, Set<Integer> likes, Mpa mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.likes = likes;
        this.mpa = mpa;
        this.genres = genres;
    }
}
