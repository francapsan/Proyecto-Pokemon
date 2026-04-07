package com.pokemon;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona las interacciones de tipo (ventajas, desventajas, etc.).
 * Esta clase centraliza la tabla de tipos para que sea fácil de extender.
 */
public class TypeChart {

    private static final Map<String, Map<String, Double>> chart = new HashMap<>();

    // Bloque estático para inicializar la tabla de tipos cuando la clase se carga.
    static {
        // AGUA
        Map<String, Double> aguaInteractions = new HashMap<>();
        aguaInteractions.put("FUEGO", 2.0);
        aguaInteractions.put("PLANTA", 0.5);
        chart.put("AGUA", aguaInteractions);

        // FUEGO
        Map<String, Double> fuegoInteractions = new HashMap<>();
        fuegoInteractions.put("PLANTA", 2.0);
        fuegoInteractions.put("AGUA", 0.5);
        chart.put("FUEGO", fuegoInteractions);

        // PLANTA
        Map<String, Double> plantaInteractions = new HashMap<>();
        plantaInteractions.put("AGUA", 2.0);
        plantaInteractions.put("FUEGO", 0.5);
        chart.put("PLANTA", plantaInteractions);
    }

    public static double getMultiplier(String attackerType, String defenderType) {
        // Devuelve el multiplicador si existe, si no, devuelve 1.0 (daño normal).
        return chart.getOrDefault(attackerType, new HashMap<>()).getOrDefault(defenderType, 1.0);
    }
}