package com.pokemon.pokeapi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.pokemon.pokeapi.PokeApiModels.MoveResponse;
import com.pokemon.pokeapi.PokeApiModels.PokemonResponse;
import com.pokemon.pokeapi.PokeApiModels.SpeciesResponse;

/**
 * Cliente HTTP de PokéAPI v2.
 * Los movimientos se cachean porque muchos Pokémon comparten los mismos, y las peticiones
 * simultáneas se limitan: la política de uso justo de PokéAPI pide no saturar el servicio.
 */
@Component
public class PokeApiClient {

    private static final int MAX_CONCURRENT_REQUESTS = 16;

    private final RestClient restClient;
    private final Map<String, MoveResponse> moveCache = new ConcurrentHashMap<>();
    private final Semaphore permits = new Semaphore(MAX_CONCURRENT_REQUESTS);

    public PokeApiClient(RestClient pokeApiRestClient) {
        this.restClient = pokeApiRestClient;
    }

    public PokemonResponse getPokemon(int id) {
        return get(PokemonResponse.class, "/pokemon/{id}", id);
    }

    public SpeciesResponse getSpecies(String name) {
        return get(SpeciesResponse.class, "/pokemon-species/{name}", name);
    }

    public MoveResponse getMove(String name) {
        MoveResponse cached = moveCache.get(name);
        if (cached != null) {
            return cached;
        }
        MoveResponse move = get(MoveResponse.class, "/move/{name}", name);
        MoveResponse previous = moveCache.putIfAbsent(name, move);
        return previous != null ? previous : move;
    }

    private <T> T get(Class<T> type, String uri, Object variable) {
        try {
            permits.acquire();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new PokeApiException("Petición a PokéAPI interrumpida en " + uri + " (" + variable + ")", ex);
        }
        try {
            T body = restClient.get().uri(uri, variable).retrieve().body(type);
            if (body == null) {
                throw new PokeApiException("Respuesta vacía de PokéAPI en " + uri + " (" + variable + ")", null);
            }
            return body;
        } catch (RestClientException ex) {
            throw new PokeApiException("Error consultando PokéAPI en " + uri + " (" + variable + ")", ex);
        } finally {
            permits.release();
        }
    }
}
