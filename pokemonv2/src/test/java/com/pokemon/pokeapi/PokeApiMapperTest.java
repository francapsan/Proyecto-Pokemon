package com.pokemon.pokeapi;

import static com.pokemon.pokeapi.PokeApiFixtures.blastoise;
import static com.pokemon.pokeapi.PokeApiFixtures.charizard;
import static com.pokemon.pokeapi.PokeApiFixtures.move;
import static com.pokemon.pokeapi.PokeApiFixtures.species;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pokemon.Attack;
import com.pokemon.AttackCategory;
import com.pokemon.PokemonMedia;
import com.pokemon.PokemonTemplate;
import com.pokemon.PokemonType;
import com.pokemon.Stats;
import com.pokemon.pokeapi.PokeApiModels.MoveResponse;
import com.pokemon.pokeapi.PokeApiModels.PokemonResponse;

class PokeApiMapperTest {

    private static final Stats BALANCED = new Stats(100, 100, 100, 100, 100, 100);

    private static List<String> names(List<Attack> attacks) {
        return attacks.stream().map(Attack::getName).toList();
    }

    @Test
    void onlyUsesLevelUpMoves() {
        List<String> names = PokeApiMapper.levelUpMoveNames(blastoise("tackle", "hydro-pump"));

        assertEquals(List.of("tackle", "hydro-pump"), names);
    }

    @Test
    void mapsSpanishNameTypeStatsAndCategory() {
        List<MoveResponse> moves = List.of(move("hydro-pump", "Hidrobomba", 110, "water", "special"));

        PokemonTemplate template = PokeApiMapper.toTemplate(
                blastoise("hydro-pump"), species("blastoise", "Blastoise"), moves);

        assertEquals("Blastoise", template.name());
        assertEquals(List.of(PokemonType.AGUA), template.types());
        assertEquals(new Stats(154, 103, 120, 105, 125, 98), template.stats());
        Attack hydroPump = template.attacks().getFirst();
        assertEquals("Hidrobomba", hydroPump.getName());
        assertEquals(110, hydroPump.getPower());
        assertEquals(AttackCategory.ESPECIAL, hydroPump.getCategory());
    }

    @Test
    void prefersAnimatedShowdownSpriteAndLatestCry() {
        PokemonMedia media = PokeApiMapper.media(blastoise());

        assertEquals(PokeApiFixtures.SPRITES + "other/showdown/9.gif", media.spriteUrl());
        assertEquals(PokeApiFixtures.CRIES + "latest/9.ogg", media.cryUrl());
    }

    @Test
    void fallsBackToDefaultSpriteAndLegacyCry() {
        PokemonMedia media = PokeApiMapper.media(charizard());

        assertEquals(PokeApiFixtures.SPRITES + "6.png", media.spriteUrl());
        assertEquals(PokeApiFixtures.CRIES + "legacy/6.ogg", media.cryUrl());
    }

    @Test
    void toleratesMissingMedia() {
        PokemonResponse bare = blastoise();
        PokemonResponse withoutMedia = new PokemonResponse(bare.id(), bare.name(), bare.species(),
                bare.types(), bare.stats(), bare.moves(), null, null);

        assertEquals(PokemonMedia.NONE, PokeApiMapper.media(withoutMedia));
    }

    @Test
    void keepsBothTypesOrderedBySlot() {
        assertEquals(List.of(PokemonType.FUEGO, PokemonType.VOLADOR), PokeApiMapper.types(charizard()));
    }

    @Test
    void picksBestMoveOfEachOwnTypeThenCoverage() {
        List<MoveResponse> moves = List.of(
                move("skull-bash", "Cabezazo", 130, "normal", "physical"),
                move("flare-blitz", "Envite Ígneo", 120, "fire", "physical"),
                move("air-slash", "Tajo Aéreo", 75, "flying", "special"),
                move("dragon-claw", "Garra Dragón", 80, "dragon", "physical"),
                move("ember", "Ascuas", 40, "fire", "special"));

        List<Attack> attacks = PokeApiMapper.selectAttacks(
                List.of(PokemonType.FUEGO, PokemonType.VOLADOR), BALANCED, moves);

        assertEquals(List.of("Envite Ígneo", "Tajo Aéreo", "Cabezazo"), names(attacks));
    }

    @Test
    void prefersMovesThatUseTheStrongerOffensiveStat() {
        Stats specialAttacker = new Stats(100, 50, 100, 150, 100, 100);
        List<MoveResponse> moves = List.of(
                move("flare-blitz", "Envite Ígneo", 120, "fire", "physical"),
                move("flamethrower", "Lanzallamas", 90, "fire", "special"));

        List<Attack> attacks = PokeApiMapper.selectAttacks(List.of(PokemonType.FUEGO), specialAttacker, moves);

        assertEquals("Lanzallamas", attacks.getFirst().getName());
    }

    @Test
    void fillsWithRepeatedTypesWhenThereIsNoVariety() {
        List<MoveResponse> moves = List.of(
                move("tackle", "Placaje", 40, "normal", "physical"),
                move("scratch", "Arañazo", 40, "normal", "physical"),
                move("pound", "Destructor", 40, "normal", "physical"),
                move("slam", "Atizar", 80, "normal", "physical"));

        List<Attack> attacks = PokeApiMapper.selectAttacks(List.of(PokemonType.NORMAL), BALANCED, moves);

        assertEquals(3, attacks.size());
        assertEquals("Atizar", attacks.getFirst().getName());
    }

    @Test
    void ignoresStatusMovesAndUnsupportedTypes() {
        List<MoveResponse> moves = List.of(
                move("growl", "Gruñido", null, "normal", "status"),
                move("shadow-rush", "Shadow Rush", 55, "shadow", "physical"));

        assertThrows(IllegalStateException.class, () -> PokeApiMapper.toTemplate(
                blastoise("growl", "shadow-rush"), species("blastoise", "Blastoise"), moves));
    }
}
