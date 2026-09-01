package com.pokemon;

import java.util.List;

/**
 * Vista serializable de un Pokémon para la API pública.
 */
public record PokemonDTO(
        String name,
        String type,
        int maxHp,
        int speed,
        List<AttackDTO> attacks
) {
    public record AttackDTO(String name, int damage, String type) {}

    public static PokemonDTO from(Pokemon pokemon) {
        List<AttackDTO> attackDtos = pokemon.getAttacks().stream()
                .map(a -> new AttackDTO(a.getName(), a.getDamage(), a.getType().name()))
                .toList();
        return new PokemonDTO(
                pokemon.getName(),
                pokemon.getType().name(),
                pokemon.getMaxHp(),
                pokemon.getSpeed(),
                attackDtos
        );
    }
}
