package com.pokemon.pokeapi;

public class PokeApiException extends RuntimeException {

    public PokeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
