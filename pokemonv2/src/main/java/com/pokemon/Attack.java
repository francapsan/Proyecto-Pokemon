package com.pokemon;

public class Attack {
    private final String name;
    private final int power;
    private final PokemonType type;
    private final AttackCategory category;

    public Attack(String name, int power, PokemonType type, AttackCategory category) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del ataque no puede estar vacío");
        }
        if (power < 0) {
            throw new IllegalArgumentException("La potencia no puede ser negativa");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo del ataque no puede ser nulo");
        }
        if (category == null) {
            throw new IllegalArgumentException("La categoría del ataque no puede ser nula");
        }
        this.name = name;
        this.power = power;
        this.type = type;
        this.category = category;
    }

    public String getName() { return name; }
    public int getPower() { return power; }
    public PokemonType getType() { return type; }
    public AttackCategory getCategory() { return category; }
}
