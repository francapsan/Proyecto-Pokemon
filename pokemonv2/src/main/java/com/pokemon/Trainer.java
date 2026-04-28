package com.pokemon;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    public static final int MAX_TEAM_SIZE = 3;

    private final String name;
    private final String gender;
    private final List<Pokemon> team;

    public Trainer(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.team = new ArrayList<>();
    }

public void addToTeam(Pokemon pokemon) {
    if (this.team.size() < MAX_TEAM_SIZE) {
        this.team.add(pokemon);
    } else {
        System.out.println("¡El equipo está lleno!");
    }
}

    public boolean hasAvailablePokemon() {
        return team.stream().anyMatch(p -> p.getHp() > 0);
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public List<Pokemon> getTeam() { return team; }
}
