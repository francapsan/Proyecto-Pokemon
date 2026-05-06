package com.pokemon;

public class Attack {
    private final String name;
    private final int damage;
    private final PokemonType type;

    public Attack(String name, int damage, PokemonType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del ataque no puede estar vacío");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo del ataque no puede ser nulo");
        }
        this.name = name;
        this.damage = damage;
        this.type = type;
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public PokemonType getType() { return type; }
}