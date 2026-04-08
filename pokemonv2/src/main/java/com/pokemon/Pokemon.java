package com.pokemon;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    private static final int MAX_ATTACKS = 3;
    private final String name;
    private final PokemonType type;
    private final int speed;
    private final List<Attack> attacks;
    
    private int hp;
    private String ownerName; // Nombre del entrenador que posee este Pokémon

    public Pokemon(String name, PokemonType type, int hp, int speed) {
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.attacks = new ArrayList<>();
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void learnAttack(Attack attack) {
        if (this.attacks.size() < MAX_ATTACKS) {
            this.attacks.add(attack);
        }
    }

    public void receiveDamage(Attack enemyAttack, Pokemon attacker) {
        double multiplier = TypeChart.getMultiplier(enemyAttack.getType(), this.type);

        if (multiplier > 1.0) {
            System.out.println("¡ES SÚPER EFECTIVO!");
        } else if (multiplier < 1.0) {
            System.out.println("No es muy efectivo...");
        }

        int finalDamage = (int) (enemyAttack.getDamage() * multiplier);
        this.hp -= finalDamage;
        if (this.hp < 0) this.hp = 0;

        System.out.println("El " + this.name + " de " + this.ownerName + 
                           " ha recibido " + finalDamage + " de daño. (HP restante: " + this.hp + ")");
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getSpeed() { return speed; }
    public List<Attack> getAttacks() { return attacks; }
    public String getOwnerName() { return ownerName; }
    public PokemonType getType() { return type; }
}