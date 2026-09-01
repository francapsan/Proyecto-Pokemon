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
        const payload = [{ name: 'Blastoise', type: 'AGUA', maxHp: 79, speed: 78, attacks: [] }];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => payload,
        });

        const data = await pokemonService.listPokemons();
        expect(data).toEqual(payload);
        expect(fetch).toHaveBeenCalledOnce();
    });

    it('lanza error cuando la respuesta no es OK', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            status: 500,
        });

        await expect(pokemonService.listPokemons()).rejects.toThrow(/500/);
    });
});
