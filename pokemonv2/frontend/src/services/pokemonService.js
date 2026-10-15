import { API_CONFIG } from '../constants/config';

const getJson = async (url) => {
    const response = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!response.ok) {
        throw new Error(`No se pudo cargar el catálogo (HTTP ${response.status})`);
    }
    return response.json();
};

/** Servicio para obtener el catálogo de Pokémon del backend, que lo saca de PokéAPI. */
export const pokemonService = {
    async listPokemons() {
        return getJson(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.POKEMONS}`);
    },

    /** `count` Pokémon distintos sorteados entre toda la Pokédex. */
    async listRandomPokemons(count) {
        return getJson(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.RANDOM_POKEMONS}?count=${count}`);
    },
};
