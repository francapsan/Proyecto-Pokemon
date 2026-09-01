package com.pokemon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PokemonTest {

    @Test
    void rejectsInvalidConstructorArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("", PokemonType.AGUA, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", null, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", PokemonType.AGUA, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Pokemon("X", PokemonType.AGUA, 10, 0));
    }

    @Test
    void enforcesMaxAttacks() {
        Pokemon p = new Pokemon("Test", PokemonType.NORMAL, 100, 50);
        p.learnAttack(new Attack("A1", 10, PokemonType.NORMAL));
        p.learnAttack(new Attack("A2", 10, PokemonType.NORMAL));
        p.learnAttack(new Attack("A3", 10, PokemonType.NORMAL));
        assertThrows(IllegalStateException.class,
                () -> p.learnAttack(new Attack("A4", 10, PokemonType.NORMAL)));
    }

    @Test
    void hpNeverBelowZero() {
        Pokemon target = new Pokemon("Target", PokemonType.PLANTA, 10, 10);
        Pokemon attacker = new Pokemon("Attacker", PokemonType.FUEGO, 10, 10);
        Attack lethal = new Attack("Overkill", 9999, PokemonType.FUEGO);
        target.receiveDamage(lethal, attacker);
        assertEquals(0, target.getHp());
    }

    @Test
    void rejectsNullAttackOrAttacker() {
        Pokemon p = new Pokemon("X", PokemonType.NORMAL, 10, 10);
        Attack a = new Attack("Golpe", 5, PokemonType.NORMAL);
        assertThrows(IllegalArgumentException.class, () -> p.receiveDamage(null, p));
        assertThrows(IllegalArgumentException.class, () -> p.receiveDamage(a, null));
    }
}
