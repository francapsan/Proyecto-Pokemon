import { API_CONFIG } from '../constants/config';

export const trainerService = {
    async createTrainer(trainerData) {
        const url = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.TRAINERS}`;
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(trainerData),
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Error al crear el entrenador');
        }

        return await response.json();
    }
};
