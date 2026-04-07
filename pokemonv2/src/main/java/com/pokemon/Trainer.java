package com.pokemon;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private String name;
    private String gender;
    private List<Pokemon> team;

    public Trainer(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.team = new ArrayList<>();
    }

public void addToTeam(Pokemon pokemon) {
    if (this.team.size() < 3) {
        pokemon.setOwnerName(this.name);
        this.team.add(pokemon);
    } else {
        System.out.println("¡El equipo está lleno!");
    }
}

    public boolean hasAvailablePokemon() {
        for (Pokemon p : team) {
            if (p.getHp() > 0) return true;
        }
        return false;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public List<Pokemon> getTeam() { return team; }
}
