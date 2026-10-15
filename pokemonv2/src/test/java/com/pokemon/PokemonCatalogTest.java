package com.pokemon;

import static com.pokemon.pokeapi.PokeApiFixtures.blastoise;
import static com.pokemon.pokeapi.PokeApiFixtures.generic;
import static com.pokemon.pokeapi.PokeApiFixtures.move;
import static com.pokemon.pokeapi.PokeApiFixtures.species;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pokemon.pokeapi.PokeApiClient;
import com.pokemon.pokeapi.PokeApiException;
import com.pokemon.pokeapi.PokeApiFixtures;
import com.pokemon.pokeapi.PokeApiProperties;

class PokemonCatalogTest {

    private PokeApiClient client;
    private PokemonCatalog catalog;

    @BeforeEach
    void setUp() {
        client = mock(PokeApiClient.class);
        catalog = new PokemonCatalog(client, properties(List.of(9), 1025));
    }

    private static PokeApiProperties properties(List<Integer> ids, int maxPokedexId) {
        return new PokeApiProperties("https://pokeapi.test", ids, maxPokedexId, Duration.ofSeconds(1), Duration.ofSeconds(1));
    }

    private void stubBlastoise() {
        when(client.getPokemon(9)).thenReturn(blastoise("hydro-pump", "skull-bash"));
        when(client.getSpecies("blastoise")).thenReturn(species("blastoise", "Blastoise"));
        when(client.getMove("hydro-pump")).thenReturn(move("hydro-pump", "Hidrobomba", 110, "water", "special"));
        when(client.getMove("skull-bash")).thenReturn(move("skull-bash", "Cabezazo", 130, "normal", "physical"));
    }

    /** Cualquier ID devuelve un Pokémon genérico "Poke{id}" jugable. */
    private PokemonCatalog randomCatalog(int maxPokedexId, long seed) {
        when(client.getPokemon(anyInt())).thenAnswer(inv -> generic(inv.getArgument(0)));
        when(client.getSpecies(anyString())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            return species(name, "P" + name.substring(1));
        });
        when(client.getMove("tackle")).thenReturn(move("tackle", "Placaje", 40, "normal", "physical"));
        return new PokemonCatalog(client, properties(List.of(), maxPokedexId), new Random(seed));
    }

    private static List<String> names(List<PokemonDTO> pokemons) {
        return pokemons.stream().map(PokemonDTO::name).toList();
    }

    @Test
    void buildsCatalogFromPokeApi() {
        stubBlastoise();

        List<PokemonDTO> pokemons = catalog.list();

        assertEquals(1, pokemons.size());
        PokemonDTO blastoise = pokemons.getFirst();
        assertEquals("Blastoise", blastoise.name());
        assertEquals(List.of("AGUA"), blastoise.types());
        assertEquals(154, blastoise.maxHp());
        assertEquals(98, blastoise.speed());
        assertEquals(PokeApiFixtures.SPRITES + "other/showdown/9.gif", blastoise.sprite());
        assertEquals(PokeApiFixtures.CRIES + "latest/9.ogg", blastoise.cry());
        assertEquals(List.of("Hidrobomba", "Cabezazo"),
                blastoise.attacks().stream().map(PokemonDTO.AttackDTO::name).toList());
    }

    @Test
    void downloadsEachSpeciesOnlyOnce() {
        stubBlastoise();

        List<PokemonDTO> first = catalog.list();
        List<PokemonDTO> second = catalog.list();

        assertNotSame(first.getFirst(), second.getFirst());
        verify(client, times(1)).getPokemon(9);
        verify(client, times(1)).getSpecies("blastoise");
    }

    @Test
    void retriesAfterAFailedDownload() {
        when(client.getPokemon(9))
                .thenThrow(new PokeApiException("caído", null))
                .thenReturn(blastoise("hydro-pump"));
        when(client.getSpecies("blastoise")).thenReturn(species("blastoise", "Blastoise"));
        when(client.getMove("hydro-pump")).thenReturn(move("hydro-pump", "Hidrobomba", 110, "water", "special"));

        assertThrows(PokeApiException.class, () -> catalog.list());
        assertEquals("Blastoise", catalog.list().getFirst().name());
    }

    @Test
    void randomReturnsDistinctPokemonFromTheWholePokedex() {
        PokemonCatalog randomCatalog = randomCatalog(1025, 42);

        List<String> names = names(randomCatalog.random(20));

        assertEquals(20, names.size());
        assertEquals(20, names.stream().distinct().count());
    }

    @Test
    void randomChangesBetweenCalls() {
        PokemonCatalog randomCatalog = randomCatalog(1025, 7);

        assertNotEquals(names(randomCatalog.random(10)), names(randomCatalog.random(10)));
    }

    @Test
    void randomReplacesPokemonThatCannotBeLoaded() {
        PokemonCatalog randomCatalog = randomCatalog(12, 1);
        when(client.getPokemon(5)).thenThrow(new PokeApiException("caído", null));

        List<String> names = names(randomCatalog.random(11));

        assertEquals(11, names.size());
        assertFalse(names.contains("Poke5"));
    }

    @Test
    void randomFailsWhenThereAreNotEnoughPlayablePokemon() {
        PokemonCatalog randomCatalog = randomCatalog(3, 1);
        when(client.getPokemon(2)).thenThrow(new PokeApiException("caído", null));

        assertThrows(PokeApiException.class, () -> randomCatalog.random(3));
    }

    @Test
    void randomRejectsInvalidCounts() {
        assertThrows(IllegalArgumentException.class, () -> catalog.random(0));
        assertThrows(IllegalArgumentException.class, () -> catalog.random(PokemonCatalog.MAX_RANDOM + 1));
    }
}
