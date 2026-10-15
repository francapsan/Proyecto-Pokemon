import { describe, expect, it } from 'vitest';
import { splitChoices } from './pokemonChoices';

describe('splitChoices', () => {
    it('da a cada jugador sus propias opciones sin repetir', () => {
        const list = Array.from({ length: 20 }, (_, i) => ({ name: `P${i}` }));

        const choices = splitChoices(list, 10);

        expect(choices[1]).toHaveLength(10);
        expect(choices[2]).toHaveLength(10);
        const p1Names = choices[1].map((p) => p.name);
        expect(choices[2].some((p) => p1Names.includes(p.name))).toBe(false);
    });
});
