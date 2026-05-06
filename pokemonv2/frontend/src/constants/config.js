// Configuración de la aplicación
export const API_CONFIG = {
    BASE_URL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    ENDPOINTS: {
        TRAINERS: '/api/trainers/create'
    }
};

export const AUDIO_CONFIG = {
    SOUNDS: {
        BACKGROUND_MUSIC: '/sounds/Theme.mp3',
        CLICK_BUTTON: '/sounds/Click Button.mp3'
    },
    VOLUME: {
        BACKGROUND: 0.3,
        CLICK: 0.7
    }
};

export const GAME_CONFIG = {
    MAX_PLAYERS: 2,
    PLAYER_SETUP_DELAY: 4000 // ms
};
