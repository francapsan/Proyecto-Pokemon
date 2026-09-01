import { describe, expect, it } from 'vitest';
import { trainerValidator } from './validators';

describe('trainerValidator', () => {
    it('valida un entrenador correcto', () => {
        const res = trainerValidator.validate({ name: 'Ash', gender: 'chico' });
        expect(res.isValid).toBe(true);
        expect(res.errors).toEqual({});
    });

    it('rechaza nombre vacío', () => {
        const res = trainerValidator.validate({ name: '   ', gender: 'chica' });
        expect(res.isValid).toBe(false);
        expect(res.errors.name).toBeDefined();
    });

    it('rechaza género no válido', () => {
        const res = trainerValidator.validate({ name: 'Misty', gender: 'x' });
        expect(res.isValid).toBe(false);
        expect(res.errors.gender).toBeDefined();
    });

    it('rechaza nombre demasiado largo', () => {
        const res = trainerValidator.validateName('a'.repeat(51));
        expect(res.isValid).toBe(false);
    });

    it('acepta género en mayúsculas', () => {
        expect(trainerValidator.validateGender('CHICO').isValid).toBe(true);
        expect(trainerValidator.validateGender('Chica').isValid).toBe(true);
    });
});
