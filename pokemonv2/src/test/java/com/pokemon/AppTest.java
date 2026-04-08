package com.pokemon;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AppTest {

    @Test
    public void testSuperEffectiveDamage() {
        Pokemon attacker = new Pokemon("Blastoise", PokemonType.AGUA, 100, 78);
        Attack waterAttack = new Attack("Hidrobomba", 40, PokemonType.AGUA);
        attacker.learnAttack(waterAttack);

        Pokemon defender = new Pokemon("Charizard", PokemonType.FUEGO, 100, 100);
        int initialHp = defender.getHp();

        defender.receiveDamage(waterAttack, attacker);

        int expectedDamage = waterAttack.getDamage() * 2;
        int finalHp = defender.getHp();

        assertEquals(initialHp - expectedDamage, finalHp);
    }

    @Test
    public void testNormalDamage() {
        Pokemon attacker = new Pokemon("Charizard", PokemonType.FUEGO, 100, 100);
        Attack fireAttack = new Attack("Lanzallamas", 40, PokemonType.FUEGO);
        attacker.learnAttack(fireAttack);

        Pokemon defender = new Pokemon("Otro Charizard", PokemonType.FUEGO, 100, 100);
        int initialHp = defender.getHp();

        defender.receiveDamage(fireAttack, attacker);

        int expectedDamage = fireAttack.getDamage();
        assertEquals(initialHp - expectedDamage, defender.getHp());
    }

    @Test
    public void testNotVeryEffectiveDamage() {
        Pokemon attacker = new Pokemon("Charizard", PokemonType.FUEGO, 100, 100);
        Attack fireAttack = new Attack("Lanzallamas", 40, PokemonType.FUEGO);
        attacker.learnAttack(fireAttack);

        Pokemon defender = new Pokemon("Blastoise", PokemonType.AGUA, 100, 78);
        int initialHp = defender.getHp();

        defender.receiveDamage(fireAttack, attacker);

        int expectedDamage = fireAttack.getDamage() / 2;
        assertEquals(initialHp - expectedDamage, defender.getHp());
    }
    @Test
    public void testTypeChartLogic() {
        assertEquals(2.0, TypeChart.getMultiplier(PokemonType.AGUA, PokemonType.FUEGO), 0.001);
        assertEquals(0.5, TypeChart.getMultiplier(PokemonType.PLANTA, PokemonType.FUEGO), 0.001);
        assertEquals(1.0, TypeChart.getMultiplier(PokemonType.FUEGO, PokemonType.FUEGO), 0.001);
    }
}
