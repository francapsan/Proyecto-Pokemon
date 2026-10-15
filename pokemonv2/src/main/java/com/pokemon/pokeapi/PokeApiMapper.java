package com.pokemon.pokeapi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.pokemon.Attack;
import com.pokemon.AttackCategory;
import com.pokemon.PokemonMedia;
import com.pokemon.PokemonTemplate;
import com.pokemon.PokemonType;
import com.pokemon.Stats;
import com.pokemon.pokeapi.PokeApiModels.Cries;
import com.pokemon.pokeapi.PokeApiModels.LocalizedName;
import com.pokemon.pokeapi.PokeApiModels.MoveResponse;
import com.pokemon.pokeapi.PokeApiModels.OtherSprites;
import com.pokemon.pokeapi.PokeApiModels.PokemonResponse;
import com.pokemon.pokeapi.PokeApiModels.Sprites;
import com.pokemon.pokeapi.PokeApiModels.SpeciesResponse;
import com.pokemon.pokeapi.PokeApiModels.StatEntry;

/**
 * Traduce las respuestas de PokéAPI al modelo del juego.
 * Aquí viven las decisiones de diseño de la integración (qué tipos, qué stats, qué ataques).
 */
public final class PokeApiMapper {

    static final String LANGUAGE = "es";
    static final int MAX_ATTACKS = 3;
    private static final String LEVEL_UP = "level-up";

    private PokeApiMapper() {}

    /** Movimientos que el Pokémon aprende subiendo de nivel en alguna edición, sin repetir. */
    public static List<String> levelUpMoveNames(PokemonResponse pokemon) {
        return pokemon.moves().stream()
                .filter(entry -> entry.versionGroupDetails().stream()
                        .anyMatch(detail -> LEVEL_UP.equals(detail.moveLearnMethod().name())))
                .map(entry -> entry.move().name())
                .distinct()
                .toList();
    }

    public static PokemonTemplate toTemplate(PokemonResponse pokemon,
                                             SpeciesResponse species,
                                             List<MoveResponse> candidateMoves) {
        List<PokemonType> types = types(pokemon);
        Stats stats = stats(pokemon);
        String name = localizedName(species.names()).orElseGet(() -> capitalize(pokemon.name()));
        List<Attack> attacks = selectAttacks(types, stats, candidateMoves);
        if (attacks.isEmpty()) {
            throw new IllegalStateException("PokéAPI no devuelve ataques ofensivos para " + pokemon.name());
        }
        return new PokemonTemplate(name, types, stats, attacks, media(pokemon));
    }

    /**
     * Sprite: GIF animado de Showdown (mismo estilo que los GIF locales del juego), si no el PNG por defecto
     * y como último recurso la ilustración oficial. Grito: el actual o, si no hay, el clásico.
     */
    static PokemonMedia media(PokemonResponse pokemon) {
        Sprites sprites = pokemon.sprites();
        String sprite = null;
        if (sprites != null) {
            OtherSprites other = sprites.other();
            sprite = firstNonBlank(
                    other == null || other.showdown() == null ? null : other.showdown().frontDefault(),
                    sprites.frontDefault(),
                    other == null || other.officialArtwork() == null ? null : other.officialArtwork().frontDefault());
        }
        Cries cries = pokemon.cries();
        String cry = cries == null ? null : firstNonBlank(cries.latest(), cries.legacy());
        return new PokemonMedia(sprite, cry);
    }

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate;
            }
        }
        return null;
    }

    /** Tipos ordenados por slot; se descartan los que no existen en combate. */
    static List<PokemonType> types(PokemonResponse pokemon) {
        List<PokemonType> types = pokemon.types().stream()
                .sorted(Comparator.comparingInt(typeSlot -> typeSlot.slot()))
                .flatMap(slot -> PokemonType.fromApiName(slot.type().name()).stream())
                .toList();
        if (types.isEmpty()) {
            throw new IllegalStateException("Tipo no soportado para " + pokemon.name());
        }
        return types;
    }

    static Stats stats(PokemonResponse pokemon) {
        return Stats.fromBaseStats(
                baseStat(pokemon, "hp"),
                baseStat(pokemon, "attack"),
                baseStat(pokemon, "defense"),
                baseStat(pokemon, "special-attack"),
                baseStat(pokemon, "special-defense"),
                baseStat(pokemon, "speed"));
    }

    private static int baseStat(PokemonResponse pokemon, String statName) {
        return pokemon.stats().stream()
                .filter(stat -> statName.equals(stat.stat().name()))
                .mapToInt(StatEntry::baseStat)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Falta la stat " + statName + " de " + pokemon.name()));
    }

    /**
     * Elige hasta {@link #MAX_ATTACKS} movimientos ofensivos, ordenados por potencia × stat ofensiva que usan
     * (así un atacante especial prefiere movimientos especiales): primero el mejor de cada uno de sus tipos
     * y después los mejores de tipos aún no cubiertos, para que el jugador tenga variedad.
     */
    static List<Attack> selectAttacks(List<PokemonType> ownTypes, Stats stats, List<MoveResponse> moves) {
        Comparator<Attack> byScore = Comparator.comparingInt((Attack attack) -> score(attack, stats)).reversed()
                .thenComparing(Attack::getName);
        List<Attack> candidates = moves.stream()
                .flatMap(move -> toAttack(move).stream())
                .sorted(byScore)
                .toList();

        List<Attack> selected = new ArrayList<>();
        Set<PokemonType> coveredTypes = EnumSet.noneOf(PokemonType.class);

        for (PokemonType ownType : ownTypes) {
            candidates.stream()
                    .filter(attack -> attack.getType() == ownType)
                    .findFirst()
                    .ifPresent(attack -> add(attack, selected, coveredTypes));
        }
        for (Attack attack : candidates) {
            if (selected.size() >= MAX_ATTACKS) break;
            if (!coveredTypes.contains(attack.getType())) {
                add(attack, selected, coveredTypes);
            }
        }
        for (Attack attack : candidates) {
            if (selected.size() >= MAX_ATTACKS) break;
            if (!selected.contains(attack)) {
                add(attack, selected, coveredTypes);
            }
        }
        return selected;
    }

    private static int score(Attack attack, Stats stats) {
        int offensiveStat = attack.getCategory() == AttackCategory.FISICO ? stats.attack() : stats.specialAttack();
        return attack.getPower() * offensiveStat;
    }

    private static void add(Attack attack, List<Attack> selected, Set<PokemonType> coveredTypes) {
        selected.add(attack);
        coveredTypes.add(attack.getType());
    }

    /** Descarta movimientos de estado (sin potencia) y tipos que no existen en combate. */
    private static Optional<Attack> toAttack(MoveResponse move) {
        if (move.power() == null || move.power() <= 0 || move.damageClass() == null) {
            return Optional.empty();
        }
        Optional<AttackCategory> category = switch (move.damageClass().name()) {
            case "physical" -> Optional.of(AttackCategory.FISICO);
            case "special" -> Optional.of(AttackCategory.ESPECIAL);
            default -> Optional.empty();
        };
        Optional<PokemonType> type = PokemonType.fromApiName(move.type().name());
        if (category.isEmpty() || type.isEmpty()) {
            return Optional.empty();
        }
        String name = localizedName(move.names()).orElseGet(() -> capitalize(move.name()));
        return Optional.of(new Attack(name, move.power(), type.get(), category.get()));
    }

    static Optional<String> localizedName(List<LocalizedName> names) {
        if (names == null) return Optional.empty();
        return names.stream()
                .filter(entry -> entry.language() != null && LANGUAGE.equals(entry.language().name()))
                .map(LocalizedName::name)
                .findFirst();
    }

    private static String capitalize(String apiName) {
        String spaced = apiName.replace('-', ' ');
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
