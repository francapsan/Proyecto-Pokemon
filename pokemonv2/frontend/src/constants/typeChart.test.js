import { describe, expect, it } from 'vitest';
import { getTypeMultiplier, getEffectivenessMessage, POKEMON_TYPES, TYPE_LABELS } from './typeChart';

describe('typeChart', () => {
    it('AGUA es super efectivo contra FUEGO', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.AGUA, POKEMON_TYPES.FUEGO)).toBe(2.0);
    });

    it('FUEGO es poco efectivo contra AGUA', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.FUEGO, POKEMON_TYPES.AGUA)).toBe(0.5);
    });

    it('devuelve 1.0 para combinaciones sin ventaja definida', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.NORMAL, POKEMON_TYPES.FUEGO)).toBe(1.0);
        expect(getTypeMultiplier(POKEMON_TYPES.DRAGON, POKEMON_TYPES.AGUA)).toBe(1.0);
    });

    it('aplica la tabla oficial a los tipos nuevos', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.FUEGO, POKEMON_TYPES.FUEGO)).toBe(0.5);
        expect(getTypeMultiplier(POKEMON_TYPES.HADA, POKEMON_TYPES.DRAGON)).toBe(2);
        expect(getTypeMultiplier(POKEMON_TYPES.ELECTRICO, POKEMON_TYPES.TIERRA)).toBe(0);
    });

    it('combina los multiplicadores contra doble tipo', () => {
        expect(getTypeMultiplier(POKEMON_TYPES.ROCA, ['FUEGO', 'VOLADOR'])).toBe(4);
        expect(getTypeMultiplier(POKEMON_TYPES.PLANTA, ['FUEGO', 'VOLADOR'])).toBe(0.25);
        expect(getTypeMultiplier(POKEMON_TYPES.TIERRA, ['FUEGO', 'VOLADOR'])).toBe(0);
    });

    it('cada tipo tiene etiqueta en español', () => {
        Object.keys(POKEMON_TYPES).forEach((type) => expect(TYPE_LABELS[type]).toBeTruthy());
    });

    it('devuelve 1.0 para tipos desconocidos', () => {
        expect(getTypeMultiplier('DESCONOCIDO', POKEMON_TYPES.AGUA)).toBe(1.0);
    });

    it('genera mensaje de efectividad correcto', () => {
        expect(getEffectivenessMessage(2.0)).toMatch(/súper efectivo/i);
        expect(getEffectivenessMessage(0.5)).toMatch(/no es muy efectivo/i);
        expect(getEffectivenessMessage(1.0)).toBeNull();
        expect(getEffectivenessMessage(0)).toMatch(/no afecta/i);
    });
});
