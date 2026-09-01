package com.pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pokemon {

    private static final Logger log = LoggerFactory.getLogger(Pokemon.class);

    private static final int MAX_ATTACKS = 3;
    private static final int MIN_STAT = 1;

    private final String name;
    private final PokemonType type;
    private final int speed;
    private final int maxHp;
    private final List<Attack> attacks;

    private int hp;

    public Pokemon(String name, PokemonType type, int hp, int speed) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del Pokémon no puede estar vacío");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo del Pokémon no puede ser nulo");
        }
        if (hp < MIN_STAT) {
            throw new IllegalArgumentException("El HP debe ser mayor a " + MIN_STAT);
        }
        if (speed < MIN_STAT) {
            throw new IllegalArgumentException("La velocidad debe ser mayor a " + MIN_STAT);
        }

        this.name = name;
        this.type = type;
        this.maxHp = hp;
        this.hp = hp;
        this.speed = speed;
        this.attacks = new ArrayList<>();
    }

    public void learnAttack(Attack attack) {
        if (attack == null) {
            throw new IllegalArgumentException("El ataque no puede ser nulo");
        }
        if (this.attacks.size() >= MAX_ATTACKS) {
            throw new IllegalStateException("¡El Pokémon ya conoce " + MAX_ATTACKS + " ataques!");
        }
        this.attacks.add(attack);
    }

    public void receiveDamage(Attack enemyAttack, Pokemon attacker) {
        if (enemyAttack == null) {
            throw new IllegalArgumentException("El ataque no puede ser nulo");
        }
        if (attacker == null) {
            throw new IllegalArgumentException("El atacante no puede ser nulo");
        }

        double multiplier = TypeChart.getMultiplier(enemyAttack.getType(), this.type);

        if (multiplier > 1.0) {
            log.info("¡ES SÚPER EFECTIVO!");
        } else if (multiplier < 1.0) {
            log.info("No es muy efectivo...");
        }

        int finalDamage = (int) (enemyAttack.getDamage() * multiplier);
        this.hp = Math.max(0, this.hp - finalDamage);

        log.info("El {} ha recibido {} de daño. (HP restante: {})", this.name, finalDamage, this.hp);
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getSpeed() { return speed; }
    public List<Attack> getAttacks() { return Collections.unmodifiableList(attacks); }
    public PokemonType getType() { return type; }
}
