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
        int finalDamage = enemyAttack.getDamage();

        // Lógica de ventaja de tipo: Solo si el ataque usado es el "insignia" (el primero)
        // y el tipo del ataque gana al tipo del defensor.
        if (enemyAttack.getName().equals(attacker.getAttacks().get(0).getName())) {
            if (hasTypeAdvantage(enemyAttack.getType(), this.type)) {
                finalDamage *= 2;
                System.out.println("¡ES SÚPER EFECTIVO!");
            }
        }

        this.hp -= finalDamage;
        if (this.hp < 0) this.hp = 0;

        // Mensaje personalizado indicando de quién es el Pokémon que sufre el daño
        System.out.println("El " + this.name + " de " + this.ownerName + 
                           " ha recibido " + finalDamage + " de daño. (HP restante: " + this.hp + ")");
    }

    // Lógica privada para comprobar debilidades elementales
    private boolean hasTypeAdvantage(String attackerType, String defenderType) {
        return (attackerType.equals("AGUA") && defenderType.equals("FUEGO")) ||
               (attackerType.equals("FUEGO") && defenderType.equals("PLANTA")) ||
               (attackerType.equals("PLANTA") && defenderType.equals("AGUA"));
    }

    // Getters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getSpeed() { return speed; }
    public List<Attack> getAttacks() { return attacks; }
    public String getOwnerName() { return ownerName; }
    public String getType() { return type; }
}