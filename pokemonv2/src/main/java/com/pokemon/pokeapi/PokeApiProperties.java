package com.pokemon.pokeapi;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @param baseUrl        URL base de PokéAPI v2.
 * @param pokemonIds     IDs de la Pokédex nacional del catálogo fijo ({@code GET /api/pokemons}), en orden.
 * @param maxPokedexId   Último ID de la Pokédex nacional entre los que se sortean los Pokémon aleatorios.
 * @param connectTimeout Tiempo máximo para establecer la conexión.
 * @param readTimeout    Tiempo máximo de espera de cada respuesta.
 */
@ConfigurationProperties(prefix = "pokeapi")
public record PokeApiProperties(
        @DefaultValue("https://pokeapi.co/api/v2") String baseUrl,
        @DefaultValue({"9", "6", "3"}) List<Integer> pokemonIds,
        @DefaultValue("1025") int maxPokedexId,
        @DefaultValue("5s") Duration connectTimeout,
        @DefaultValue("10s") Duration readTimeout
) {}
