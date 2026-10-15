package com.pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pokemon.pokeapi.PokeApiClient;
import com.pokemon.pokeapi.PokeApiException;
import com.pokemon.pokeapi.PokeApiMapper;
import com.pokemon.pokeapi.PokeApiModels.MoveResponse;
import com.pokemon.pokeapi.PokeApiModels.PokemonResponse;
import com.pokemon.pokeapi.PokeApiModels.SpeciesResponse;
import com.pokemon.pokeapi.PokeApiProperties;

/**
 * Catálogo de Pokémon disponibles en el juego, alimentado por PokéAPI.
 * Punto único de verdad para el backend: el frontend consume {@code GET /api/pokemons}
 * y {@code GET /api/pokemons/random} para no duplicar esta información en JavaScript.
 * <p>
 * Cada especie se descarga una sola vez y se guarda como {@link PokemonTemplate};
 * si la descarga falla no se cachea nada y se reintenta en la siguiente petición.
 */
@Component
public class PokemonCatalog {

    public static final int MAX_RANDOM = 30;

    private static final Logger log = LoggerFactory.getLogger(PokemonCatalog.class);

    private final PokeApiClient client;
    private final PokeApiProperties properties;
    private final RandomGenerator random;
    private final Map<Integer, PokemonTemplate> templates = new ConcurrentHashMap<>();

    @Autowired
    public PokemonCatalog(PokeApiClient client, PokeApiProperties properties) {
        this(client, properties, RandomGenerator.getDefault());
    }

    PokemonCatalog(PokeApiClient client, PokeApiProperties properties, RandomGenerator random) {
        this.client = client;
        this.properties = properties;
        this.random = random;
    }

    public List<PokemonDTO> list() {
        return properties.pokemonIds().stream()
                .map(this::template)
                .map(PokemonTemplate::toDto)
                .toList();
    }

    /**
     * {@code count} Pokémon distintos sorteados entre toda la Pokédex.
     * Los que no se pueden jugar (p. ej. Ditto, que solo tiene movimientos de estado) o fallan al
     * descargarse se sustituyen por otros sorteados.
     */
    public List<PokemonDTO> random(int count) {
        if (count < 1 || count > MAX_RANDOM) {
            throw new IllegalArgumentException("Se pueden pedir entre 1 y " + MAX_RANDOM + " Pokémon aleatorios");
        }

        List<Integer> candidates = new ArrayList<>(IntStream.rangeClosed(1, properties.maxPokedexId()).boxed().toList());
        Collections.shuffle(candidates, random);

        List<PokemonTemplate> chosen = new ArrayList<>(count);
        int next = 0;
        while (chosen.size() < count && next < candidates.size()) {
            int batchEnd = Math.min(candidates.size(), next + count - chosen.size());
            chosen.addAll(loadAll(candidates.subList(next, batchEnd)));
            next = batchEnd;
        }
        if (chosen.size() < count) {
            throw new PokeApiException("Solo se pudieron cargar " + chosen.size() + " de " + count + " Pokémon", null);
        }
        return chosen.stream().map(PokemonTemplate::toDto).toList();
    }

    /** Carga en paralelo; los Pokémon que fallan se descartan (se registran en el log). */
    private List<PokemonTemplate> loadAll(List<Integer> pokedexIds) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<PokemonTemplate>> futures = pokedexIds.stream()
                    .map(id -> executor.submit(() -> template(id)))
                    .toList();

            List<PokemonTemplate> loaded = new ArrayList<>(futures.size());
            for (int i = 0; i < futures.size(); i++) {
                try {
                    loaded.add(futures.get(i).get());
                } catch (ExecutionException ex) {
                    log.warn("Se descarta el Pokémon #{}: {}", pokedexIds.get(i), ex.getCause().getMessage());
                }
            }
            return loaded;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new PokeApiException("Carga de Pokémon interrumpida", ex);
        }
    }

    private PokemonTemplate template(int pokedexId) {
        PokemonTemplate cached = templates.get(pokedexId);
        if (cached != null) {
            return cached;
        }
        PokemonTemplate loaded = load(pokedexId);
        PokemonTemplate previous = templates.putIfAbsent(pokedexId, loaded);
        return previous != null ? previous : loaded;
    }

    private PokemonTemplate load(int pokedexId) {
        PokemonResponse pokemon = client.getPokemon(pokedexId);
        SpeciesResponse species = client.getSpecies(pokemon.species().name());
        List<MoveResponse> moves = fetchMoves(PokeApiMapper.levelUpMoveNames(pokemon));

        PokemonTemplate template = PokeApiMapper.toTemplate(pokemon, species, moves);
        log.info("Cargado {} desde PokéAPI ({} movimientos evaluados)", template.name(), moves.size());
        return template;
    }

    /** Las peticiones de movimientos son independientes, así que se lanzan en paralelo con hilos virtuales. */
    private List<MoveResponse> fetchMoves(List<String> moveNames) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<MoveResponse>> futures = moveNames.stream()
                    .map(name -> executor.submit(() -> client.getMove(name)))
                    .toList();

            List<MoveResponse> moves = new ArrayList<>(futures.size());
            for (Future<MoveResponse> future : futures) {
                moves.add(future.get());
            }
            return moves;
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new PokeApiException("Error descargando movimientos de PokéAPI", ex.getCause());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new PokeApiException("Descarga de movimientos interrumpida", ex);
        }
    }
}
