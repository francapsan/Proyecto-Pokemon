package com.pokemon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PokemonController.class)
@Import(PokemonCatalog.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsCatalog() throws Exception {
        mockMvc.perform(get("/api/pokemons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Blastoise"))
                .andExpect(jsonPath("$[0].type").value("AGUA"))
                .andExpect(jsonPath("$[0].attacks", org.hamcrest.Matchers.hasSize(3)));
    }
}
