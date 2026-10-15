package com.pokemon;

/**
 * Recursos visuales y de sonido de una especie. Ambas URLs pueden ser null:
 * el frontend recurre entonces a sus ficheros locales en {@code /media} y {@code /sounds}.
 */
public record PokemonMedia(String spriteUrl, String cryUrl) {

    public static final PokemonMedia NONE = new PokemonMedia(null, null);
}
