import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { trainerService } from './trainerService';

describe('trainerService.createTrainer', () => {
    beforeEach(() => {
        vi.stubGlobal('fetch', vi.fn());
    });

    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('serializa el body y devuelve el entrenador creado', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ name: 'Ash', gender: 'chico', team: [] }),
        });

        const result = await trainerService.createTrainer({ name: 'Ash', gender: 'chico' });

        expect(result.name).toBe('Ash');
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining('/api/trainers/create'),
            expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({ 'Content-Type': 'application/json' }),
                body: JSON.stringify({ name: 'Ash', gender: 'chico' }),
            }),
        );
    });

    it('propaga el mensaje de error del backend', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: async () => 'El nombre no puede estar vacío.',
        });

        await expect(
            trainerService.createTrainer({ name: '', gender: 'chico' }),
        ).rejects.toThrow(/nombre/i);
    });
});
