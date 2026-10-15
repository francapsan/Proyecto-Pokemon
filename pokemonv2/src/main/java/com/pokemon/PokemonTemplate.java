package com.pokemon;

import java.util.List;

/**
 * Datos inmutables de una especie, a partir de los cuales se crean Pokémon nuevos
 * (cada uno con su propio HP) sin volver a consultar la fuente de datos.
 */
public record PokemonTemplate(String name, List<PokemonType> types, Stats stats, List<Attack> attacks,
                              PokemonMedia media) {

    public PokemonTemplate {
        types = List.copyOf(types);
        attacks = List.copyOf(attacks);
        media = media == null ? PokemonMedia.NONE : media;
    }

    public Pokemon create() {
        Pokemon pokemon = new Pokemon(name, types, stats);
        attacks.forEach(pokemon::learnAttack);
        return pokemon;
    }

    public PokemonDTO toDto() {
        return PokemonDTO.from(create(), media);
    }
}
