package com.pokemon.pokeapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Subconjunto de las respuestas de PokéAPI v2 que usa el juego.
 * Se ignoran el resto de campos para no acoplarnos a todo el esquema.
 */
public final class PokeApiModels {

    private PokeApiModels() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NamedResource(String name, String url) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LocalizedName(String name, NamedResource language) {}

    /** {@code GET /pokemon/{id}} */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonResponse(
            int id,
            String name,
            NamedResource species,
            List<TypeSlot> types,
            List<StatEntry> stats,
            List<MoveEntry> moves,
            Sprites sprites,
            Cries cries
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sprites(@JsonProperty("front_default") String frontDefault, OtherSprites other) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OtherSprites(
            SpriteSet showdown,
            @JsonProperty("official-artwork") SpriteSet officialArtwork
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpriteSet(@JsonProperty("front_default") String frontDefault) {}

    /** URLs de los gritos en .ogg; {@code legacy} solo existe para las primeras generaciones. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cries(String latest, String legacy) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TypeSlot(int slot, NamedResource type) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StatEntry(@JsonProperty("base_stat") int baseStat, NamedResource stat) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MoveEntry(
            NamedResource move,
            @JsonProperty("version_group_details") List<VersionGroupDetail> versionGroupDetails
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VersionGroupDetail(
            @JsonProperty("level_learned_at") int levelLearnedAt,
            @JsonProperty("move_learn_method") NamedResource moveLearnMethod,
            @JsonProperty("version_group") NamedResource versionGroup
    ) {}

    /** {@code GET /pokemon-species/{nombre}} */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpeciesResponse(int id, String name, List<LocalizedName> names) {}

    /** {@code GET /move/{nombre}}. {@code power} es null en movimientos de estado. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MoveResponse(
            int id,
            String name,
            Integer power,
            NamedResource type,
            @JsonProperty("damage_class") NamedResource damageClass,
            List<LocalizedName> names
    ) {}
}
