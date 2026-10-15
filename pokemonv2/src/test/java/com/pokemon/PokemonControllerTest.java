package com.pokemon;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pokemon.pokeapi.PokeApiException;

@WebMvcTest(PokemonController.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PokemonCatalog catalog;

    @Test
    void returnsCatalog() throws Exception {
        Pokemon charizard = TestPokemons.charizard();
        charizard.learnAttack(new Attack("Lanzallamas", 90, PokemonType.FUEGO, AttackCategory.ESPECIAL));
        charizard.learnAttack(new Attack("Garra Dragón", 80, PokemonType.DRAGON, AttackCategory.FISICO));
        PokemonMedia media = new PokemonMedia("https://sprites/showdown/6.gif", "https://cries/latest/6.ogg");
        when(catalog.list()).thenReturn(List.of(PokemonDTO.from(charizard, media)));

        mockMvc.perform(get("/api/pokemons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Charizard"))
                .andExpect(jsonPath("$[0].types[0]").value("FUEGO"))
                .andExpect(jsonPath("$[0].types[1]").value("VOLADOR"))
                .andExpect(jsonPath("$[0].maxHp").value(153))
                .andExpect(jsonPath("$[0].specialAttack").value(129))
                .andExpect(jsonPath("$[0].speed").value(120))
                .andExpect(jsonPath("$[0].attacks[0].power").value(90))
                .andExpect(jsonPath("$[0].attacks[1].category").value("FISICO"))
                .andExpect(jsonPath("$[0].sprite").value("https://sprites/showdown/6.gif"))
                .andExpect(jsonPath("$[0].cry").value("https://cries/latest/6.ogg"));
    }

    @Test
    void returnsRandomPokemonWithDefaultCountOfTen() throws Exception {
        when(catalog.random(10)).thenReturn(List.of(PokemonDTO.from(TestPokemons.blastoise())));

        mockMvc.perform(get("/api/pokemons/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Blastoise"));
    }

    @Test
    void forwardsRandomCountAndRejectsInvalidOnes() throws Exception {
        when(catalog.random(20)).thenReturn(List.of());
        when(catalog.random(99)).thenThrow(new IllegalArgumentException("Se pueden pedir entre 1 y 30"));

        mockMvc.perform(get("/api/pokemons/random").param("count", "20"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/pokemons/random").param("count", "99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsServiceUnavailableWhenPokeApiFails() throws Exception {
        when(catalog.list()).thenThrow(new PokeApiException("caído", null));

        mockMvc.perform(get("/api/pokemons"))
                .andExpect(status().isServiceUnavailable());
    }
}
