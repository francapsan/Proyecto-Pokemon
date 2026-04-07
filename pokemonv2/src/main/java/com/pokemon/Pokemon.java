package com.pokemon;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    // Atributos finales (no cambian después de crearse)
    private final String name;
    private final String type;
    private final int speed;
    private final List<Attack> attacks;
    
    // Atributos variables
    private int hp;
    private String ownerName; // Nombre del entrenador que posee este Pokémon

    public Pokemon(String name, String type, int hp, int speed) {
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.attacks = new ArrayList<>();
    }

    // Método para asignar el dueño (se llamará desde la clase Trainer)
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void learnAttack(Attack attack) {
        if (this.attacks.size() < 3) {
            this.attacks.add(attack);
        }
    }

    public void receiveDamage(Attack enemyAttack, Pokemon attacker) {
        // Obtenemos el multiplicador de la nueva clase centralizada
        double multiplier = TypeChart.getMultiplier(enemyAttack.getType(), this.type);

        if (multiplier > 1.0) {
            System.out.println("¡ES SÚPER EFECTIVO!");
        } else if (multiplier < 1.0) {
            System.out.println("No es muy efectivo...");
        }

        int finalDamage = (int) (enemyAttack.getDamage() * multiplier);
        this.hp -= finalDamage;
        if (this.hp < 0) this.hp = 0;

        // Mensaje personalizado indicando de quién es el Pokémon que sufre el daño
        System.out.println("El " + this.name + " de " + this.ownerName + 
                           " ha recibido " + finalDamage + " de daño. (HP restante: " + this.hp + ")");
    }

    // Getters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getSpeed() { return speed; }
    public List<Attack> getAttacks() { return attacks; }
    public String getOwnerName() { return ownerName; }
    public String getType() { return type; }
}