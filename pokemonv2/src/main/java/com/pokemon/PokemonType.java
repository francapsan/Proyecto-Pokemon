package com.pokemon;

import java.util.Arrays;
import java.util.Optional;

public enum PokemonType {
    NORMAL("normal"),
    FUEGO("fire"),
    AGUA("water"),
    PLANTA("grass"),
    ELECTRICO("electric"),
    HIELO("ice"),
    LUCHA("fighting"),
    VENENO("poison"),
    TIERRA("ground"),
    VOLADOR("flying"),
    PSIQUICO("psychic"),
    BICHO("bug"),
    ROCA("rock"),
    FANTASMA("ghost"),
    DRAGON("dragon"),
    SINIESTRO("dark"),
    ACERO("steel"),
    HADA("fairy");

    /** Nombre del tipo en PokéAPI (p. ej. {@code "fire"}). */
    private final String apiName;

    PokemonType(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() { return apiName; }

    /**
     * PokéAPI incluye tipos que no existen en combate ({@code "unknown"}, {@code "shadow"}, {@code "stellar"}),
     * por eso se devuelve vacío en lugar de lanzar excepción.
     */
    public static Optional<PokemonType> fromApiName(String apiName) {
        return Arrays.stream(values())
                .filter(type -> type.apiName.equalsIgnoreCase(apiName))
                .findFirst();
    }
}
