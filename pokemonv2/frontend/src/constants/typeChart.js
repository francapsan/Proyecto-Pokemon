// Tipos de Pokémon. Coincide 1:1 con com.pokemon.PokemonType.java
export const POKEMON_TYPES = {
    AGUA: 'AGUA',
    FUEGO: 'FUEGO',
    PLANTA: 'PLANTA',
    NORMAL: 'NORMAL',
};

// Tabla de efectividad. Replica com.pokemon.TypeChart.java
// chart[atacante][defensor] = multiplicador
const TYPE_CHART = {
    AGUA:   { FUEGO: 2.0, PLANTA: 0.5 },
    FUEGO:  { PLANTA: 2.0, AGUA: 0.5 },
    PLANTA: { AGUA: 2.0, FUEGO: 0.5 },
    NORMAL: {},
};

export const getTypeMultiplier = (attackerType, defenderType) => {
    return TYPE_CHART[attackerType]?.[defenderType] ?? 1.0;
};

export const getEffectivenessMessage = (multiplier) => {
    if (multiplier > 1) return '¡Es súper efectivo!';
    if (multiplier < 1) return 'No es muy efectivo...';
    return null;
};

// Traducción del tipo a su nombre visible en español
export const TYPE_LABELS = {
    AGUA: 'Agua',
    FUEGO: 'Fuego',
    PLANTA: 'Planta',
    NORMAL: 'Normal',
};
