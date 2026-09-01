package com.pokemon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrainerTest {

    private Pokemon makePokemon(String name, int hp) {
        return new Pokemon(name, PokemonType.NORMAL, hp, 50);
    }

    @Test
    void rejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Trainer("  ", "chico"));
        assertThrows(IllegalArgumentException.class, () -> new Trainer(null, "chica"));
    }

    @Test
    void rejectsInvalidGender() {
        assertThrows(IllegalArgumentException.class, () -> new Trainer("Ash", "otro"));
        assertThrows(IllegalArgumentException.class, () -> new Trainer("Ash", null));
    }

    @Test
    void normalizesNameAndGender() {
        Trainer t = new Trainer("  Misty  ", "CHICA");
        assertEquals("Misty", t.getName());
        assertEquals("chica", t.getGender());
    }

    @Test
    void enforcesMaxTeamSize() {
        Trainer t = new Trainer("Ash", "chico");
        for (int i = 0; i < Trainer.MAX_TEAM_SIZE; i++) {
            t.addToTeam(makePokemon("P" + i, 10));
        }
        assertThrows(IllegalStateException.class, () -> t.addToTeam(makePokemon("Extra", 10)));
    }

    @Test
    void hasAvailablePokemonReflectsHp() {
        Trainer t = new Trainer("Ash", "chico");
        Pokemon alive = makePokemon("Alive", 10);
        t.addToTeam(alive);
        assertTrue(t.hasAvailablePokemon());

        Attack hit = new Attack("Golpe", 10, PokemonType.NORMAL);
        alive.receiveDamage(hit, alive);
        assertFalse(t.hasAvailablePokemon());
    }

    @Test
    void getTeamReturnsDefensiveCopy() {
        Trainer t = new Trainer("Ash", "chico");
        t.addToTeam(makePokemon("Pika", 20));
        t.getTeam().clear();
        assertEquals(1, t.getTeam().size(), "Modificar la lista devuelta no debe afectar al Trainer");
    }
}
