package com.pokemon.pokeapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.pokemon.pokeapi.PokeApiModels.MoveResponse;
import com.pokemon.pokeapi.PokeApiModels.PokemonResponse;

class PokeApiClientTest {

    private static final String BASE_URL = "https://pokeapi.test/api/v2";

    private MockRestServiceServer server;
    private PokeApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new PokeApiClient(builder.build());
    }

    @Test
    void parsesPokemonIgnoringUnknownFields() {
        server.expect(requestTo(BASE_URL + "/pokemon/9")).andRespond(withSuccess("""
                {
                  "id": 9, "name": "blastoise", "height": 16,
                  "species": {"name": "blastoise", "url": "u"},
                  "types": [{"slot": 1, "type": {"name": "water", "url": "u"}}],
                  "stats": [{"base_stat": 79, "effort": 0, "stat": {"name": "hp", "url": "u"}}],
                  "moves": [{
                    "move": {"name": "hydro-pump", "url": "u"},
                    "version_group_details": [{
                      "level_learned_at": 42,
                      "move_learn_method": {"name": "level-up", "url": "u"},
                      "version_group": {"name": "red-blue", "url": "u"},
                      "order": null
                    }]
                  }],
                  "sprites": {
                    "front_default": "https://sprites/9.png",
                    "back_default": "https://sprites/back/9.png",
                    "other": {
                      "showdown": {"front_default": "https://sprites/showdown/9.gif"},
                      "official-artwork": {"front_default": "https://sprites/artwork/9.png"}
                    }
                  },
                  "cries": {"latest": "https://cries/latest/9.ogg", "legacy": "https://cries/legacy/9.ogg"}
                }
                """, MediaType.APPLICATION_JSON));

        PokemonResponse pokemon = client.getPokemon(9);

        assertEquals("https://sprites/showdown/9.gif", pokemon.sprites().other().showdown().frontDefault());
        assertEquals("https://sprites/artwork/9.png", pokemon.sprites().other().officialArtwork().frontDefault());
        assertEquals("https://cries/latest/9.ogg", pokemon.cries().latest());

        assertEquals("blastoise", pokemon.species().name());
        assertEquals(79, pokemon.stats().getFirst().baseStat());
        assertEquals("level-up", pokemon.moves().getFirst().versionGroupDetails().getFirst().moveLearnMethod().name());
        server.verify();
    }

    @Test
    void cachesMovesAndKeepsNullPower() {
        server.expect(once(), requestTo(BASE_URL + "/move/growl")).andRespond(withSuccess("""
                {"id": 45, "name": "growl", "power": null,
                 "type": {"name": "normal", "url": "u"},
                 "damage_class": {"name": "status", "url": "u"},
                 "names": [{"name": "Gruñido", "language": {"name": "es", "url": "u"}}]}
                """, MediaType.APPLICATION_JSON));

        MoveResponse first = client.getMove("growl");
        MoveResponse second = client.getMove("growl");

        assertNull(first.power());
        assertEquals("status", first.damageClass().name());
        assertEquals(first, second);
        server.verify();
    }

    @Test
    void wrapsHttpErrors() {
        server.expect(requestTo(BASE_URL + "/pokemon/9")).andRespond(withServerError());

        assertThrows(PokeApiException.class, () -> client.getPokemon(9));
    }
}
