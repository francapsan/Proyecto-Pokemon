package com.pokemon;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

    private Pokemon blastoise;
    private Pokemon charizard;
    private Attack waterAttack;
    private Attack fireAttack;

    @Before
    public void setUp() {
        blastoise = new Pokemon("Blastoise", PokemonType.AGUA, 100, 78);
        waterAttack = new Attack("Hidrobomba", 40, PokemonType.AGUA);
        blastoise.learnAttack(waterAttack);

        charizard = new Pokemon("Charizard", PokemonType.FUEGO, 100, 100);
        fireAttack = new Attack("Lanzallamas", 40, PokemonType.FUEGO);
        charizard.learnAttack(fireAttack);
    }

    @Test
    public void testSuperEffectiveDamage() {
        int initialHp = charizard.getHp();
        charizard.receiveDamage(waterAttack, blastoise);

        int expectedDamage = waterAttack.getDamage() * 2;
        assertEquals(initialHp - expectedDamage, charizard.getHp());
    }

    @Test
    public void testNormalDamage() {
        Pokemon anotherCharizard = new Pokemon("Otro Charizard", PokemonType.FUEGO, 100, 100);
        int initialHp = anotherCharizard.getHp();

        anotherCharizard.receiveDamage(fireAttack, charizard);

        int expectedDamage = fireAttack.getDamage();
        assertEquals(initialHp - expectedDamage, anotherCharizard.getHp());
    }

    @Test
    public void testNotVeryEffectiveDamage() {
        int initialHp = blastoise.getHp();
        blastoise.receiveDamage(fireAttack, charizard);

        int expectedDamage = (int) (fireAttack.getDamage() * 0.5); // Usamos 0.5 para consistencia con TypeChart
        assertEquals(initialHp - expectedDamage, blastoise.getHp());
    }
    @Test
    public void testTypeChartLogic() {
        assertEquals(2.0, TypeChart.getMultiplier(PokemonType.AGUA, PokemonType.FUEGO), 0.001);
        assertEquals(0.5, TypeChart.getMultiplier(PokemonType.PLANTA, PokemonType.FUEGO), 0.001);
        assertEquals(1.0, TypeChart.getMultiplier(PokemonType.FUEGO, PokemonType.FUEGO), 0.001);
    }
}
