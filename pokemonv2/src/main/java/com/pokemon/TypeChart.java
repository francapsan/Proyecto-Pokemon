package com.pokemon;

import static com.pokemon.PokemonType.*;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Tabla de efectividad oficial (generación 6 en adelante), extraída de
 * {@code damage_relations} en {@code https://pokeapi.co/api/v2/type/{nombre}}.
 * Se mantiene en código porque no cambia y así el combate no depende de la red.
 */
public class TypeChart {

    private static final Map<PokemonType, Map<PokemonType, Double>> chart = new EnumMap<>(PokemonType.class);

    static {
        relations(NORMAL,    List.of(),                                         List.of(ROCA, ACERO),                                          List.of(FANTASMA));
        relations(FUEGO,     List.of(BICHO, ACERO, PLANTA, HIELO),              List.of(ROCA, FUEGO, AGUA, DRAGON),                            List.of());
        relations(AGUA,      List.of(TIERRA, ROCA, FUEGO),                      List.of(AGUA, PLANTA, DRAGON),                                 List.of());
        relations(PLANTA,    List.of(TIERRA, ROCA, AGUA),                       List.of(VOLADOR, VENENO, BICHO, ACERO, FUEGO, PLANTA, DRAGON), List.of());
        relations(ELECTRICO, List.of(VOLADOR, AGUA),                            List.of(PLANTA, ELECTRICO, DRAGON),                            List.of(TIERRA));
        relations(HIELO,     List.of(VOLADOR, TIERRA, PLANTA, DRAGON),          List.of(ACERO, FUEGO, AGUA, HIELO),                            List.of());
        relations(LUCHA,     List.of(NORMAL, ROCA, ACERO, HIELO, SINIESTRO),    List.of(VOLADOR, VENENO, BICHO, PSIQUICO, HADA),               List.of(FANTASMA));
        relations(VENENO,    List.of(PLANTA, HADA),                             List.of(VENENO, TIERRA, ROCA, FANTASMA),                       List.of(ACERO));
        relations(TIERRA,    List.of(VENENO, ROCA, ACERO, FUEGO, ELECTRICO),    List.of(BICHO, PLANTA),                                        List.of(VOLADOR));
        relations(VOLADOR,   List.of(LUCHA, BICHO, PLANTA),                     List.of(ROCA, ACERO, ELECTRICO),                               List.of());
        relations(PSIQUICO,  List.of(LUCHA, VENENO),                            List.of(ACERO, PSIQUICO),                                      List.of(SINIESTRO));
        relations(BICHO,     List.of(PLANTA, PSIQUICO, SINIESTRO),              List.of(LUCHA, VOLADOR, VENENO, FANTASMA, ACERO, FUEGO, HADA), List.of());
        relations(ROCA,      List.of(VOLADOR, BICHO, FUEGO, HIELO),             List.of(LUCHA, TIERRA, ACERO),                                 List.of());
        relations(FANTASMA,  List.of(FANTASMA, PSIQUICO),                       List.of(SINIESTRO),                                            List.of(NORMAL));
        relations(DRAGON,    List.of(DRAGON),                                   List.of(ACERO),                                                List.of(HADA));
        relations(SINIESTRO, List.of(FANTASMA, PSIQUICO),                       List.of(LUCHA, SINIESTRO, HADA),                               List.of());
        relations(ACERO,     List.of(ROCA, HIELO, HADA),                        List.of(ACERO, FUEGO, AGUA, ELECTRICO),                        List.of());
        relations(HADA,      List.of(LUCHA, DRAGON, SINIESTRO),                 List.of(VENENO, ACERO, FUEGO),                                 List.of());
    }

    private static void relations(PokemonType attacker,
                                  List<PokemonType> doubleDamageTo,
                                  List<PokemonType> halfDamageTo,
                                  List<PokemonType> noDamageTo) {
        Map<PokemonType, Double> interactions = new EnumMap<>(PokemonType.class);
        doubleDamageTo.forEach(defender -> interactions.put(defender, 2.0));
        halfDamageTo.forEach(defender -> interactions.put(defender, 0.5));
        noDamageTo.forEach(defender -> interactions.put(defender, 0.0));
        chart.put(attacker, interactions);
    }

    public static double getMultiplier(PokemonType attackerType, PokemonType defenderType) {
        return chart.getOrDefault(attackerType, Collections.emptyMap())
                    .getOrDefault(defenderType, 1.0);
    }

    /** Contra un Pokémon de doble tipo se multiplican ambos efectos (de ×0 a ×4). */
    public static double getMultiplier(PokemonType attackerType, List<PokemonType> defenderTypes) {
        return defenderTypes.stream()
                .mapToDouble(defenderType -> getMultiplier(attackerType, defenderType))
                .reduce(1.0, (a, b) -> a * b);
    }
}
