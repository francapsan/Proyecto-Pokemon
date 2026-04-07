package com.pokemon;

import java.util.HashMap;
import java.util.Map;

public class TypeChart {

    private static final Map<String, Map<String, Double>> chart = new HashMap<>();

    static {
        Map<String, Double> aguaInteractions = new HashMap<>();
        aguaInteractions.put("FUEGO", 2.0);
        aguaInteractions.put("PLANTA", 0.5);
        chart.put("AGUA", aguaInteractions);

        Map<String, Double> fuegoInteractions = new HashMap<>();
        fuegoInteractions.put("PLANTA", 2.0);
        fuegoInteractions.put("AGUA", 0.5);
        chart.put("FUEGO", fuegoInteractions);

        Map<String, Double> plantaInteractions = new HashMap<>();
        plantaInteractions.put("AGUA", 2.0);
        plantaInteractions.put("FUEGO", 0.5);
        chart.put("PLANTA", plantaInteractions);
    }

    public static double getMultiplier(String attackerType, String defenderType) {
        return chart.getOrDefault(attackerType, new HashMap<>()).getOrDefault(defenderType, 1.0);
    }
}