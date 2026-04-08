package com.pokemon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypeChart {

    private static final Map<PokemonType, Map<PokemonType, Double>> chart = new HashMap<>();

    static {
        Map<PokemonType, Double> aguaInteractions = new HashMap<>();
        aguaInteractions.put(PokemonType.FUEGO, 2.0);
        aguaInteractions.put(PokemonType.PLANTA, 0.5);
        chart.put(PokemonType.AGUA, aguaInteractions);

        Map<PokemonType, Double> fuegoInteractions = new HashMap<>();
        fuegoInteractions.put(PokemonType.PLANTA, 2.0);
        fuegoInteractions.put(PokemonType.AGUA, 0.5);
        chart.put(PokemonType.FUEGO, fuegoInteractions);

        Map<PokemonType, Double> plantaInteractions = new HashMap<>();
        plantaInteractions.put(PokemonType.AGUA, 2.0);
        plantaInteractions.put(PokemonType.FUEGO, 0.5);
        chart.put(PokemonType.PLANTA, plantaInteractions);
    }

    public static double getMultiplier(PokemonType attackerType, PokemonType defenderType) {
        return chart.getOrDefault(attackerType, Collections.emptyMap()).getOrDefault(defenderType, 1.0);
    }
}