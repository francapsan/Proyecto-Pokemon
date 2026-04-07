package com.pokemon;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Pruebas unitarias para la lógica del juego Pokémon.
 */
public class AppTest {

    /**
     * Prueba que el daño súper efectivo (x2) se aplica correctamente.
     * Agua -> Fuego
     */
    @Test
    public void testSuperEffectiveDamage() {
        // Arrange: Preparamos el escenario
        Pokemon attacker = new Pokemon("Blastoise", "AGUA", 100, 78);
        Attack waterAttack = new Attack("Hidrobomba", 40, "AGUA");
        attacker.learnAttack(waterAttack); // El ataque insignia debe ser el primero

        Pokemon defender = new Pokemon("Charizard", "FUEGO", 100, 100);
        int initialHp = defender.getHp();

        // Act: Ejecutamos la acción a probar
        defender.receiveDamage(waterAttack, attacker);

        // Assert: Verificamos el resultado
        int expectedDamage = waterAttack.getDamage() * 2;
        int finalHp = defender.getHp();

        // Comprobamos que el HP final es el inicial menos el daño esperado
        assertEquals(initialHp - expectedDamage, finalHp);
    }

    /**
     * Prueba que el daño normal (x1) se aplica correctamente.
     * Usaremos un matchup neutral, como Fuego vs. un tipo no relacionado (p.ej. Fuego).
     */
    @Test
    public void testNormalDamage() {
        // Arrange
        Pokemon attacker = new Pokemon("Charizard", "FUEGO", 100, 100);
        Attack fireAttack = new Attack("Lanzallamas", 40, "FUEGO");
        attacker.learnAttack(fireAttack);

        // Un Pokémon del mismo tipo o de un tipo no relacionado recibirá daño normal.
        Pokemon defender = new Pokemon("Otro Charizard", "FUEGO", 100, 100);
        int initialHp = defender.getHp();

        // Act
        defender.receiveDamage(fireAttack, attacker);

        // Assert
        int expectedDamage = fireAttack.getDamage(); // Multiplicador x1
        assertEquals(initialHp - expectedDamage, defender.getHp());
    }

    /**
     * Prueba que el daño poco efectivo (x0.5) se aplica correctamente.
     * Fuego -> Agua
     */
    @Test
    public void testNotVeryEffectiveDamage() {
        // Arrange
        Pokemon attacker = new Pokemon("Charizard", "FUEGO", 100, 100);
        Attack fireAttack = new Attack("Lanzallamas", 40, "FUEGO");
        attacker.learnAttack(fireAttack);

        Pokemon defender = new Pokemon("Blastoise", "AGUA", 100, 78);
        int initialHp = defender.getHp();

        // Act
        defender.receiveDamage(fireAttack, attacker);

        // Assert: El daño debería ser la mitad
        int expectedDamage = fireAttack.getDamage() / 2;
        assertEquals(initialHp - expectedDamage, defender.getHp());
    }

    /**
     * Prueba la lógica de la clase TypeChart de forma aislada para verificar
     * que los multiplicadores son correctos. Esta es la prueba para nuestra última actualización.
     */
    @Test
    public void testTypeChartLogic() {
        // Assert: Verificamos directamente los resultados del TypeChart

        // Caso Súper Efectivo (x2.0)
        assertEquals(2.0, TypeChart.getMultiplier("AGUA", "FUEGO"), 0.001);

        // Caso Poco Efectivo (x0.5)
        assertEquals(0.5, TypeChart.getMultiplier("PLANTA", "FUEGO"), 0.001);

        // Caso Neutral (x1.0) - No hay una regla definida
        assertEquals(1.0, TypeChart.getMultiplier("FUEGO", "FUEGO"), 0.001);
    }
}
