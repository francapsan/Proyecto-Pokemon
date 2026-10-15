import { describe, expect, it } from 'vitest';
import { calculateDamage } from './damage';

// Mismos casos que src/test/java/com/pokemon/AppTest.java: si cambia un número aquí, tiene que cambiar allí.
// Stats reales a nivel 50 (las de TestPokemons.java).
const blastoise = { types: ['AGUA'], attack: 103, defense: 120, specialAttack: 105, specialDefense: 125 };
const charizard = { types: ['FUEGO', 'VOLADOR'], attack: 104, defense: 98, specialAttack: 129, specialDefense: 105 };
const venusaur = { types: ['PLANTA', 'VENENO'], attack: 102, defense: 103, specialAttack: 120, specialDefense: 120 };

const hydroPump = { name: 'Hidrobomba', power: 110, type: 'AGUA', category: 'ESPECIAL' };
const flamethrower = { name: 'Lanzallamas', power: 90, type: 'FUEGO', category: 'ESPECIAL' };
const tackle = { name: 'Placaje', power: 40, type: 'NORMAL', category: 'FISICO' };

describe('calculateDamage', () => {
    it('súper efectivo con STAB', () => {
        expect(calculateDamage(hydroPump, blastoise, charizard)).toEqual({ damage: 150, effectiveness: 2 });
    });

    it('súper efectivo contra doble tipo', () => {
        expect(calculateDamage(flamethrower, charizard, venusaur).damage).toBe(132);
    });

    it('poco efectivo', () => {
        expect(calculateDamage(flamethrower, charizard, blastoise)).toEqual({ damage: 31, effectiveness: 0.5 });
    });

    it('neutro, físico y sin STAB', () => {
        expect(calculateDamage(tackle, blastoise, charizard).damage).toBe(20);
    });

    it('inmunidad no hace daño', () => {
        const gengar = { types: ['FANTASMA', 'VENENO'], defense: 50, specialDefense: 50 };
        expect(calculateDamage(tackle, blastoise, gengar)).toEqual({ damage: 0, effectiveness: 0 });
    });

    it('resistido ×0.25 hace al menos 1 de daño', () => {
        const weak = { name: 'Absorber', power: 1, type: 'PLANTA', category: 'ESPECIAL' };
        expect(calculateDamage(weak, blastoise, charizard)).toEqual({ damage: 1, effectiveness: 0.25 });
    });
});
