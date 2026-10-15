package com.pokemon.pokeapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pokemon.pokeapi.PokeApiModels.Cries;
import com.pokemon.pokeapi.PokeApiModels.LocalizedName;
import com.pokemon.pokeapi.PokeApiModels.OtherSprites;
import com.pokemon.pokeapi.PokeApiModels.SpriteSet;
import com.pokemon.pokeapi.PokeApiModels.Sprites;
import com.pokemon.pokeapi.PokeApiModels.MoveEntry;
import com.pokemon.pokeapi.PokeApiModels.MoveResponse;
import com.pokemon.pokeapi.PokeApiModels.NamedResource;
import com.pokemon.pokeapi.PokeApiModels.PokemonResponse;
import com.pokemon.pokeapi.PokeApiModels.SpeciesResponse;
import com.pokemon.pokeapi.PokeApiModels.StatEntry;
import com.pokemon.pokeapi.PokeApiModels.TypeSlot;
import com.pokemon.pokeapi.PokeApiModels.VersionGroupDetail;

/** Respuestas de PokéAPI construidas en memoria para los tests. */
public final class PokeApiFixtures {

    private PokeApiFixtures() {}

    public static NamedResource ref(String name) {
        return new NamedResource(name, "https://pokeapi.co/api/v2/x/" + name + "/");
    }

    /** Blastoise con los movimientos indicados aprendidos por nivel, más Rayo Hielo aprendido solo por MT. */
    public static PokemonResponse blastoise(String... levelUpMoves) {
        List<MoveEntry> moves = new ArrayList<>();
        Arrays.stream(levelUpMoves).forEach(move -> moves.add(new MoveEntry(ref(move), List.of(
                new VersionGroupDetail(36, ref("level-up"), ref("red-blue")),
                new VersionGroupDetail(0, ref("machine"), ref("sword-shield"))))));
        moves.add(new MoveEntry(ref("ice-beam"), List.of(
                new VersionGroupDetail(0, ref("machine"), ref("red-blue")))));
        return new PokemonResponse(9, "blastoise", ref("blastoise"),
                List.of(new TypeSlot(1, ref("water"))),
                stats(79, 83, 100, 85, 105, 78),
                moves,
                new Sprites(SPRITES + "9.png", new OtherSprites(
                        new SpriteSet(SPRITES + "other/showdown/9.gif"),
                        new SpriteSet(SPRITES + "other/official-artwork/9.png"))),
                new Cries(CRIES + "latest/9.ogg", CRIES + "legacy/9.ogg"));
    }

    /**
     * Charizard sin movimientos, con los slots de tipo desordenados para comprobar que se respeta el slot,
     * sin GIF de Showdown y sin grito actual (para probar los recursos alternativos).
     */
    public static PokemonResponse charizard() {
        return new PokemonResponse(6, "charizard", ref("charizard"),
                List.of(new TypeSlot(2, ref("flying")), new TypeSlot(1, ref("fire"))),
                stats(78, 84, 78, 109, 85, 100),
                List.of(),
                new Sprites(SPRITES + "6.png", new OtherSprites(new SpriteSet(null), null)),
                new Cries(null, CRIES + "legacy/6.ogg"));
    }

    /** Pokémon genérico "poke{id}" de tipo Normal que aprende Placaje por nivel. */
    public static PokemonResponse generic(int id) {
        String name = "poke" + id;
        return new PokemonResponse(id, name, ref(name),
                List.of(new TypeSlot(1, ref("normal"))),
                stats(50, 50, 50, 50, 50, 50),
                List.of(new MoveEntry(ref("tackle"), List.of(
                        new VersionGroupDetail(1, ref("level-up"), ref("red-blue"))))),
                new Sprites(SPRITES + id + ".png", null),
                new Cries(CRIES + "latest/" + id + ".ogg", null));
    }

    public static final String SPRITES = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";
    public static final String CRIES = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/";

    private static List<StatEntry> stats(int hp, int attack, int defense, int spAttack, int spDefense, int speed) {
        return List.of(
                new StatEntry(hp, ref("hp")),
                new StatEntry(attack, ref("attack")),
                new StatEntry(defense, ref("defense")),
                new StatEntry(spAttack, ref("special-attack")),
                new StatEntry(spDefense, ref("special-defense")),
                new StatEntry(speed, ref("speed")));
    }

    public static SpeciesResponse species(String apiName, String spanishName) {
        return new SpeciesResponse(1, apiName, List.of(
                new LocalizedName(apiName.toUpperCase(), ref("ja")),
                new LocalizedName(spanishName, ref("es"))));
    }

    public static MoveResponse move(String apiName, String spanishName, Integer power, String type, String damageClass) {
        return new MoveResponse(1, apiName, power, ref(type), ref(damageClass), List.of(
                new LocalizedName(apiName, ref("en")),
                new LocalizedName(spanishName, ref("es"))));
    }
}
