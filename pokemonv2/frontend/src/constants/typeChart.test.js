import { describe, expect, it } from 'vitest';
import { getTypeMultiplier, getEffectivenessMessage, POKEMON_TYPES } from './typeChart';

describe('typeChart', () => {
    it('AGUA es super efectivo contra FUEGO', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.AGUA, POKEMON_TYPES.FUEGO)).toBe(2.0);
    });

    it('FUEGO es poco efectivo contra AGUA', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.FUEGO, POKEMON_TYPES.AGUA)).toBe(0.5);
    });

    it('devuelve 1.0 para combinaciones sin ventaja definida', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.NORMAL, POKEMON_TYPES.FUEGO)).toBe(1.0);
        expect(getTypeMultiplier(POKEMON_TYPES.FUEGO, POKEMON_TYPES.FUEGO)).toBe(1.0);
    });

    it('devuelve 1.0 para tipos desconocidos', () => {
        expect(getTypeMultiplier('DESCONOCIDO', POKEMON_TYPES.AGUA)).toBe(1.0);
    });

    it('genera mensaje de efectividad correcto', () => {
        expect(getEffectivenessMessage(2.0)).toMatch(/súper efectivo/i);
        expect(getEffectivenessMessage(0.5)).toMatch(/no es muy efectivo/i);
        expect(getEffectivenessMessage(1.0)).toBeNull();
    });
});
