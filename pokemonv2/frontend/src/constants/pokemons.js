import { POKEMON_TYPES } from './typeChart';

// Catálogo de Pokémon. Replica com.pokemon.Main.java
// (HP ajustados a 78/79/80 según frontend)
export const POKEMON_CATALOG = {
    Charizard: {
        name: 'Charizard',
        type: POKEMON_TYPES.FUEGO,
        hp: 78,
        speed: 100,
        attacks: [
            { name: 'Lanzallamas',  damage: 40, type: POKEMON_TYPES.FUEGO  },
            { name: 'Cola Dragón',  damage: 20, type: POKEMON_TYPES.NORMAL },
            { name: 'Vuelo',        damage: 25, type: POKEMON_TYPES.NORMAL },
        ],
    },
    Blastoise: {
        name: 'Blastoise',
        type: POKEMON_TYPES.AGUA,
        hp: 79,
        speed: 78,
        attacks: [
            { name: 'Hidrobomba',   damage: 40, type: POKEMON_TYPES.AGUA   },
            { name: 'Mordisco',     damage: 20, type: POKEMON_TYPES.NORMAL },
            { name: 'Puño Certero', damage: 25, type: POKEMON_TYPES.NORMAL },
        ],
    },
    Venusaur: {
        name: 'Venusaur',
        type: POKEMON_TYPES.PLANTA,
        hp: 80,
        speed: 80,
        attacks: [
            { name: 'Rayo Solar',   damage: 40, type: POKEMON_TYPES.PLANTA },
            { name: 'Hoja Afilada', damage: 20, type: POKEMON_TYPES.NORMAL },
            { name: 'Terremoto',    damage: 25, type: POKEMON_TYPES.NORMAL },
        ],
    },
};

// Lista plana para iterar (selección, listados, etc.)
export const POKEMON_LIST = Object.values(POKEMON_CATALOG);

// Devuelve los datos completos de un Pokémon dado su nombre
export const getPokemonData = (name) => POKEMON_CATALOG[name] || null;
