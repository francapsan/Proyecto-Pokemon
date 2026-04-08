package com.pokemon;

public class Attack {
    private String name;
    private int damage;
    private PokemonType type;

    public Attack(String name, int damage, PokemonType type) {
        this.name = name;
        this.damage = damage;
        this.type = type;
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public PokemonType getType() { return type; }
}