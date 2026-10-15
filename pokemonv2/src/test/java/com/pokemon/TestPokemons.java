package com.pokemon;

import java.util.List;

/** Pokémon con sus stats base oficiales, calculadas a nivel 50 como en el juego. */
final class TestPokemons {

    private TestPokemons() {}

    /** Agua · HP 154, At 103, Def 120, AtEsp 105, DefEsp 125, Vel 98 */
    static Pokemon blastoise() {
        return new Pokemon("Blastoise", List.of(PokemonType.AGUA), Stats.fromBaseStats(79, 83, 100, 85, 105, 78));
    }

    /** Fuego/Volador · HP 153, At 104, Def 98, AtEsp 129, DefEsp 105, Vel 120 */
    static Pokemon charizard() {
        return new Pokemon("Charizard", List.of(PokemonType.FUEGO, PokemonType.VOLADOR),
                Stats.fromBaseStats(78, 84, 78, 109, 85, 100));
    }

    /** Planta/Veneno · HP 155, At 102, Def 103, AtEsp 120, DefEsp 120, Vel 100 */
    static Pokemon venusaur() {
        return new Pokemon("Venusaur", List.of(PokemonType.PLANTA, PokemonType.VENENO),
                Stats.fromBaseStats(80, 82, 83, 100, 100, 80));
    }

    static Pokemon simple(String name, int hp, PokemonType... types) {
        return new Pokemon(name, List.of(types), new Stats(hp, 50, 50, 50, 50, 50));
    }
}
