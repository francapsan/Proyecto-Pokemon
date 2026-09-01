package com.pokemon;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

/**
 * Catálogo de Pokémon disponibles en el juego.
 * Punto único de verdad para el backend: el frontend consume {@code GET /api/pokemons}
 * para no duplicar esta información en JavaScript.
 */
@Component
public class PokemonCatalog {

    private final Map<Integer, Supplier<Pokemon>> creators = new LinkedHashMap<>();

    public PokemonCatalog() {
        creators.put(1, () -> {
            Pokemon p = new Pokemon("Blastoise", PokemonType.AGUA, 79, 78);
            p.learnAttack(new Attack("Hidrobomba", 40, PokemonType.AGUA));
            p.learnAttack(new Attack("Mordisco", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Puño Certero", 25, PokemonType.NORMAL));
            return p;
        });
        creators.put(2, () -> {
            Pokemon p = new Pokemon("Charizard", PokemonType.FUEGO, 78, 100);
            p.learnAttack(new Attack("Lanzallamas", 40, PokemonType.FUEGO));
            p.learnAttack(new Attack("Cola Dragon", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Vuelo", 25, PokemonType.NORMAL));
            return p;
        });
        creators.put(3, () -> {
            Pokemon p = new Pokemon("Venusaur", PokemonType.PLANTA, 80, 80);
            p.learnAttack(new Attack("Rayo Solar", 40, PokemonType.PLANTA));
            p.learnAttack(new Attack("Hoja Afilada", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Terremoto", 25, PokemonType.NORMAL));
            return p;
        });
    }

    public List<PokemonDTO> list() {
        return creators.values().stream()
                .map(supplier -> supplier.get())
                .map(pokemon -> PokemonDTO.from(pokemon))
                .toList();
    }
}
