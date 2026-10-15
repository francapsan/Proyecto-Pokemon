package com.pokemon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class PokemonTest {

    private static final Stats STATS = new Stats(10, 10, 10, 10, 10, 10);

    @Test
    void rejectsInvalidConstructorArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("", List.of(PokemonType.AGUA), STATS));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", null, STATS));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", List.of(), STATS));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", List.of(PokemonType.AGUA, PokemonType.FUEGO, PokemonType.PLANTA), STATS));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", List.of(PokemonType.AGUA, PokemonType.AGUA), STATS));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", List.of(PokemonType.AGUA), null));
    }

    @Test
    void rejectsInvalidStats() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(0, 10, 10, 10, 10, 10));
        assertThrows(IllegalArgumentException.class, () -> new Stats(10, 10, 10, 10, 10, 0));
    }

    @Test
    void rejectsInvalidAttack() {
        assertThrows(IllegalArgumentException.class,
                () -> new Attack("Golpe", -1, PokemonType.NORMAL, AttackCategory.FISICO));
        assertThrows(IllegalArgumentException.class,
                () -> new Attack("Golpe", 10, PokemonType.NORMAL, null));
    }

    @Test
    void enforcesMaxAttacks() {
        Pokemon p = TestPokemons.simple("Test", 100, PokemonType.NORMAL);
        p.learnAttack(new Attack("A1", 10, PokemonType.NORMAL, AttackCategory.FISICO));
        p.learnAttack(new Attack("A2", 10, PokemonType.NORMAL, AttackCategory.FISICO));
        p.learnAttack(new Attack("A3", 10, PokemonType.NORMAL, AttackCategory.FISICO));
        assertThrows(IllegalStateException.class,
                () -> p.learnAttack(new Attack("A4", 10, PokemonType.NORMAL, AttackCategory.FISICO)));
    }

    @Test
    void hpNeverBelowZero() {
        Pokemon target = TestPokemons.simple("Target", 10, PokemonType.PLANTA);
        Pokemon attacker = TestPokemons.simple("Attacker", 10, PokemonType.FUEGO);
        Attack lethal = new Attack("Overkill", 9999, PokemonType.FUEGO, AttackCategory.ESPECIAL);
        target.receiveDamage(lethal, attacker);
        assertEquals(0, target.getHp());
    }

    @Test
    void rejectsNullAttackOrAttacker() {
        Pokemon p = TestPokemons.simple("X", 10, PokemonType.NORMAL);
        Attack a = new Attack("Golpe", 5, PokemonType.NORMAL, AttackCategory.FISICO);
        assertThrows(IllegalArgumentException.class, () -> p.receiveDamage(null, p));
        assertThrows(IllegalArgumentException.class, () -> p.receiveDamage(a, null));
    }
}
