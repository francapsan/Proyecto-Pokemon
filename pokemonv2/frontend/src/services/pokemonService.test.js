import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { pokemonService } from './pokemonService';

describe('pokemonService', () => {
    beforeEach(() => {
        vi.stubGlobal('fetch', vi.fn());
    });

    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('devuelve el catálogo cuando la respuesta es 200', async () => {
        const payload = [{
            name: 'Blastoise', types: ['AGUA'],
            maxHp: 154, attack: 103, defense: 120, specialAttack: 105, specialDefense: 125, speed: 98,
            attacks: [{ name: 'Envite Acuático', power: 120, type: 'AGUA', category: 'FISICO' }],
        }];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => payload,
        });

        const data = await pokemonService.listPokemons();
        expect(data).toEqual(payload);
        expect(fetch).toHaveBeenCalledOnce();
    });

    it('pide los Pokémon aleatorios con el número indicado', async () => {
        fetch.mockResolvedValueOnce({ ok: true, json: async () => [] });

        await pokemonService.listRandomPokemons(20);

        expect(fetch.mock.calls[0][0]).toMatch(/\/api\/pokemons\/random\?count=20$/);
    });

    it('lanza error cuando la respuesta no es OK', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            status: 500,
        });

        await expect(pokemonService.listPokemons()).rejects.toThrow(/500/);
    });
});
