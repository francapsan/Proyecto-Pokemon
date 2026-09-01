import { API_CONFIG } from '../constants/config';

/**
 * Servicio para obtener el catálogo de Pokémon del backend.
 * El backend es el punto único de verdad; `constants/pokemons.js`
 * queda como fallback en caso de fallo de red o backend caído.
 */
export const pokemonService = {
    async listPokemons() {
        const url = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.POKEMONS}`;
        const response = await fetch(url, { headers: { 'Accept': 'application/json' } });
        if (!response.ok) {
            throw new Error(`No se pudo cargar el catálogo (HTTP ${response.status})`);
        }
        return response.json();
    },
};
