package com.pokemon;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    private static final Map<Integer, Supplier<Pokemon>> POKEMON_CREATORS = new LinkedHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    static {
        POKEMON_CREATORS.put(1, () -> {
            Pokemon p = new Pokemon("Blastoise", PokemonType.AGUA, 100, 78);
            p.learnAttack(new Attack("Hidrobomba", 40, PokemonType.AGUA));
            p.learnAttack(new Attack("Mordisco", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Puño Certero", 25, PokemonType.NORMAL));
            return p;
        });
        POKEMON_CREATORS.put(2, () -> {
            Pokemon p = new Pokemon("Charizard", PokemonType.FUEGO, 100, 100);
            p.learnAttack(new Attack("Lanzallamas", 40, PokemonType.FUEGO));
            p.learnAttack(new Attack("Cola Dragon", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Vuelo", 25, PokemonType.NORMAL));
            return p;
        });
        POKEMON_CREATORS.put(3, () -> {
            Pokemon p = new Pokemon("Venusaur", PokemonType.PLANTA, 100, 80);
            p.learnAttack(new Attack("Rayo Solar", 40, PokemonType.PLANTA));
            p.learnAttack(new Attack("Hoja Afilada", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Terremoto", 25, PokemonType.NORMAL));
            return p;
        });
    }
}