package com.pokemon;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    public static final int MAX_TEAM_SIZE = 3;

    private final String name;
    private final String gender;
    private final List<Pokemon> team;

    public Trainer(String name, String gender) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del entrenador no puede estar vacío");
        }
        if (gender == null || (!gender.equalsIgnoreCase("chico") && !gender.equalsIgnoreCase("chica"))) {
            throw new IllegalArgumentException("El género debe ser 'chico' o 'chica'");
        }
        this.name = name.trim();
        this.gender = gender.toLowerCase();
        this.team = new ArrayList<>();
    }

    public void addToTeam(Pokemon pokemon) {
        if (pokemon == null) {
            throw new IllegalArgumentException("El Pokémon no puede ser nulo");
        }
        if (this.team.size() >= MAX_TEAM_SIZE) {
            throw new IllegalStateException("¡El equipo está lleno! Máximo " + MAX_TEAM_SIZE + " Pokémon");
        }
        this.team.add(pokemon);
    }

    public boolean hasAvailablePokemon() {
        return team.stream().anyMatch(p -> p.getHp() > 0);
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public List<Pokemon> getTeam() { return new ArrayList<>(team); }
}
