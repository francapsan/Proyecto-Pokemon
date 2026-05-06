package com.pokemon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypeChart {

    private static final Map<PokemonType, Map<PokemonType, Double>> chart = new HashMap<>();

    static {
        // AGUA: efectivo contra FUEGO, débil contra PLANTA
        addInteractions(PokemonType.AGUA, Map.of(
            PokemonType.FUEGO, 2.0,
            PokemonType.PLANTA, 0.5
        ));

        // FUEGO: efectivo contra PLANTA, débil contra AGUA
        addInteractions(PokemonType.FUEGO, Map.of(
            PokemonType.PLANTA, 2.0,
            PokemonType.AGUA, 0.5
        ));

        // PLANTA: efectivo contra AGUA, débil contra FUEGO
        addInteractions(PokemonType.PLANTA, Map.of(
            PokemonType.AGUA, 2.0,
            PokemonType.FUEGO, 0.5
        ));

        // NORMAL: sin ventajas ni desventajas específicas
        addInteractions(PokemonType.NORMAL, Collections.emptyMap());
    }

    private static void addInteractions(PokemonType type, Map<PokemonType, Double> interactions) {
        chart.put(type, new HashMap<>(interactions));
    }

    public static double getMultiplier(PokemonType attackerType, PokemonType defenderType) {
        return chart.getOrDefault(attackerType, Collections.emptyMap())
                    .getOrDefault(defenderType, 1.0);
    }
}