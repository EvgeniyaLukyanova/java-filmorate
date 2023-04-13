package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Mpa {
    @NotNull
    private int id;
    private String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

}