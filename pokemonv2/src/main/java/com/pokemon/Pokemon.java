package com.pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pokemon {

    private static final Logger log = LoggerFactory.getLogger(Pokemon.class);

    private static final int MAX_ATTACKS = 3;
    private static final int MAX_TYPES = 2;

    private final String name;
    private final List<PokemonType> types;
    private final Stats stats;
    private final List<Attack> attacks;

    private int hp;

    public Pokemon(String name, List<PokemonType> types, Stats stats) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del Pokémon no puede estar vacío");
        }
        if (types == null || types.isEmpty() || types.size() > MAX_TYPES || types.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("El Pokémon debe tener uno o dos tipos");
        }
        if (types.stream().distinct().count() != types.size()) {
            throw new IllegalArgumentException("Los tipos del Pokémon no pueden repetirse");
        }
        if (stats == null) {
            throw new IllegalArgumentException("Las stats del Pokémon no pueden ser nulas");
        }

        this.name = name;
        this.types = List.copyOf(types);
        this.stats = stats;
        this.hp = stats.hp();
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

    public DamageCalculator.Result receiveDamage(Attack enemyAttack, Pokemon attacker) {
        if (enemyAttack == null) {
            throw new IllegalArgumentException("El ataque no puede ser nulo");
        }
        if (attacker == null) {
            throw new IllegalArgumentException("El atacante no puede ser nulo");
        }

        DamageCalculator.Result result = DamageCalculator.calculate(enemyAttack, attacker, this);
        double multiplier = result.effectiveness();

        if (multiplier == 0.0) {
            log.info("No afecta a {}...", this.name);
        } else if (multiplier > 1.0) {
            log.info("¡ES SÚPER EFECTIVO!");
        } else if (multiplier < 1.0) {
            log.info("No es muy efectivo...");
        }

        this.hp = Math.max(0, this.hp - result.damage());

        log.info("El {} ha recibido {} de daño. (HP restante: {})", this.name, result.damage(), this.hp);
        return result;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return stats.hp(); }
    public int getSpeed() { return stats.speed(); }
    public Stats getStats() { return stats; }
    public List<Attack> getAttacks() { return Collections.unmodifiableList(attacks); }
    public List<PokemonType> getTypes() { return types; }
    public PokemonType getPrimaryType() { return types.getFirst(); }
}
