package com.pokemon;

import java.util.List;

/**
 * Vista serializable de un Pokémon para la API pública.
 * Incluye todas las stats porque el combate se calcula en el cliente.
 * {@code sprite} y {@code cry} son URLs absolutas o null si la fuente de datos no las tiene.
 */
public record PokemonDTO(
        String name,
        List<String> types,
        int maxHp,
        int attack,
        int defense,
        int specialAttack,
        int specialDefense,
        int speed,
        List<AttackDTO> attacks,
        String sprite,
        String cry
) {
    public record AttackDTO(String name, int power, String type, String category) {}

    public static PokemonDTO from(Pokemon pokemon) {
        return from(pokemon, PokemonMedia.NONE);
    }

    public static PokemonDTO from(Pokemon pokemon, PokemonMedia media) {
        List<AttackDTO> attackDtos = pokemon.getAttacks().stream()
                .map(a -> new AttackDTO(a.getName(), a.getPower(), a.getType().name(), a.getCategory().name()))
                .toList();
        Stats stats = pokemon.getStats();
        return new PokemonDTO(
                pokemon.getName(),
                pokemon.getTypes().stream().map(Enum::name).toList(),
                stats.hp(),
                stats.attack(),
                stats.defense(),
                stats.specialAttack(),
                stats.specialDefense(),
                stats.speed(),
                attackDtos,
                media.spriteUrl(),
                media.cryUrl()
        );
    }
}
