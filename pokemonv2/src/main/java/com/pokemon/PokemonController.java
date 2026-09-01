package com.pokemon;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    private final PokemonCatalog catalog;

    public PokemonController(PokemonCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public List<PokemonDTO> listPokemons() {
        return catalog.list();
    }
}
