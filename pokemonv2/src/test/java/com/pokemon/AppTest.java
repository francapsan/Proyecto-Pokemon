package com.pokemon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Los mismos casos de daño están en {@code frontend/src/constants/damage.test.js}:
 * si cambia un número aquí, tiene que cambiar allí.
 */
public class AppTest {

    private Pokemon blastoise;
    private Pokemon charizard;
    private Pokemon venusaur;
    private Attack hydroPump;
    private Attack flamethrower;
    private Attack tackle;

    @BeforeEach
    public void setUp() {
        blastoise = TestPokemons.blastoise();
        charizard = TestPokemons.charizard();
        venusaur = TestPokemons.venusaur();
        hydroPump = new Attack("Hidrobomba", 110, PokemonType.AGUA, AttackCategory.ESPECIAL);
        flamethrower = new Attack("Lanzallamas", 90, PokemonType.FUEGO, AttackCategory.ESPECIAL);
        tackle = new Attack("Placaje", 40, PokemonType.NORMAL, AttackCategory.FISICO);
    }

    @Test
    public void testStatsAtLevel50() {
        assertEquals(new Stats(154, 103, 120, 105, 125, 98), blastoise.getStats());
        assertEquals(154, blastoise.getHp());
        assertEquals(98, blastoise.getSpeed());
    }

    @Test
    public void testSuperEffectiveDamage() {
        DamageCalculator.Result result = charizard.receiveDamage(hydroPump, blastoise);

        assertEquals(2.0, result.effectiveness(), 0.001);
        assertEquals(150, result.damage());
        assertEquals(3, charizard.getHp());
    }

    @Test
    public void testSuperEffectiveAgainstDualType() {
        assertEquals(132, DamageCalculator.calculate(flamethrower, charizard, venusaur).damage());
    }

    @Test
    public void testNotVeryEffectiveDamage() {
        DamageCalculator.Result result = DamageCalculator.calculate(flamethrower, charizard, blastoise);

        assertEquals(0.5, result.effectiveness(), 0.001);
        assertEquals(31, result.damage());
    }

    @Test
    public void testNeutralPhysicalDamageWithoutStab() {
        assertEquals(20, DamageCalculator.calculate(tackle, blastoise, charizard).damage());
    }

    @Test
    public void testImmuneDamage() {
        Pokemon gengar = TestPokemons.simple("Gengar", 135, PokemonType.FANTASMA, PokemonType.VENENO);

        DamageCalculator.Result result = gengar.receiveDamage(tackle, blastoise);

        assertEquals(0, result.damage());
        assertEquals(135, gengar.getHp());
    }

    @Test
    public void testResistedDamageIsAtLeastOne() {
        Attack weak = new Attack("Absorber", 1, PokemonType.PLANTA, AttackCategory.ESPECIAL);

        DamageCalculator.Result result = DamageCalculator.calculate(weak, blastoise, charizard);

        assertEquals(0.25, result.effectiveness(), 0.001);
        assertEquals(1, result.damage());
    }

    @Test
    public void testTypeChartLogic() {
        assertEquals(2.0, TypeChart.getMultiplier(PokemonType.AGUA, PokemonType.FUEGO), 0.001);
        assertEquals(0.5, TypeChart.getMultiplier(PokemonType.PLANTA, PokemonType.FUEGO), 0.001);
        assertEquals(0.5, TypeChart.getMultiplier(PokemonType.FUEGO, PokemonType.FUEGO), 0.001);
        assertEquals(1.0, TypeChart.getMultiplier(PokemonType.NORMAL, PokemonType.FUEGO), 0.001);
        assertEquals(0.0, TypeChart.getMultiplier(PokemonType.ELECTRICO, PokemonType.TIERRA), 0.001);
        assertEquals(2.0, TypeChart.getMultiplier(PokemonType.HADA, PokemonType.DRAGON), 0.001);
    }

    @Test
    public void testTypeChartCombinesDualTypes() {
        List<PokemonType> fireFlying = List.of(PokemonType.FUEGO, PokemonType.VOLADOR);
        assertEquals(4.0, TypeChart.getMultiplier(PokemonType.ROCA, fireFlying), 0.001);
        assertEquals(0.25, TypeChart.getMultiplier(PokemonType.PLANTA, fireFlying), 0.001);
        assertEquals(0.0, TypeChart.getMultiplier(PokemonType.TIERRA, fireFlying), 0.001);
    }

    @Test
    public void testTypeChartCoversEveryAttackingType() {
        for (PokemonType attacker : PokemonType.values()) {
            for (PokemonType defender : PokemonType.values()) {
                double multiplier = TypeChart.getMultiplier(attacker, defender);
                assertTrue(
                        multiplier == 0.0 || multiplier == 0.5 || multiplier == 1.0 || multiplier == 2.0,
                        attacker + " -> " + defender + " = " + multiplier);
            }
        }
    }

    @Test
    public void testTypesMapFromPokeApiNames() {
        assertEquals(PokemonType.PLANTA, PokemonType.fromApiName("grass").orElseThrow());
        assertEquals(PokemonType.SINIESTRO, PokemonType.fromApiName("dark").orElseThrow());
        assertTrue(PokemonType.fromApiName("stellar").isEmpty());
    }
}
