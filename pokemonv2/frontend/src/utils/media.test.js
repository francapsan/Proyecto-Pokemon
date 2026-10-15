import { afterEach, describe, expect, it, vi } from 'vitest';
import { playCry } from './media';

describe('playCry', () => {
    afterEach(() => {
        vi.unstubAllGlobals();
    });

    const stubAudio = (play = () => Promise.resolve()) => {
        const created = [];
        vi.stubGlobal('Audio', class {
            constructor(src) { this.src = src; created.push(this); }
            play() { return play(); }
        });
        return created;
    };

    it('reproduce el grito de PokéAPI con el volumen indicado', () => {
        const created = stubAudio();

        playCry({ name: 'Gengar', cry: 'https://cries/94.ogg' }, 0.4);

        expect(created).toHaveLength(1);
        expect(created[0].src).toBe('https://cries/94.ogg');
        expect(created[0].volume).toBe(0.4);
    });

    it('no hace nada si el Pokémon no tiene grito', () => {
        const created = stubAudio();

        playCry({ name: 'Gengar', cry: null });

        expect(created).toHaveLength(0);
    });

    it('si el grito no carga, solo avisa por consola', async () => {
        stubAudio(() => Promise.reject(new Error('404')));
        const warn = vi.spyOn(console, 'warn').mockImplementation(() => {});

        playCry({ name: 'Gengar', cry: 'https://cries/94.ogg' });

        await vi.waitFor(() => expect(warn).toHaveBeenCalled());
        warn.mockRestore();
    });
});
