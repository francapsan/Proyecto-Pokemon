// Tipos de Pokémon. Coincide 1:1 con com.pokemon.PokemonType.java
export const POKEMON_TYPES = {
    NORMAL: 'NORMAL',
    FUEGO: 'FUEGO',
    AGUA: 'AGUA',
    PLANTA: 'PLANTA',
    ELECTRICO: 'ELECTRICO',
    HIELO: 'HIELO',
    LUCHA: 'LUCHA',
    VENENO: 'VENENO',
    TIERRA: 'TIERRA',
    VOLADOR: 'VOLADOR',
    PSIQUICO: 'PSIQUICO',
    BICHO: 'BICHO',
    ROCA: 'ROCA',
    FANTASMA: 'FANTASMA',
    DRAGON: 'DRAGON',
    SINIESTRO: 'SINIESTRO',
    ACERO: 'ACERO',
    HADA: 'HADA',
};

// Tabla de efectividad oficial. Replica com.pokemon.TypeChart.java
// chart[atacante][defensor] = multiplicador (las combinaciones ausentes valen 1.0)
const TYPE_CHART = {
    NORMAL:    { ROCA: 0.5, ACERO: 0.5, FANTASMA: 0 },
    FUEGO:     { BICHO: 2, ACERO: 2, PLANTA: 2, HIELO: 2, ROCA: 0.5, FUEGO: 0.5, AGUA: 0.5, DRAGON: 0.5 },
    AGUA:      { TIERRA: 2, ROCA: 2, FUEGO: 2, AGUA: 0.5, PLANTA: 0.5, DRAGON: 0.5 },
    PLANTA:    { TIERRA: 2, ROCA: 2, AGUA: 2, VOLADOR: 0.5, VENENO: 0.5, BICHO: 0.5, ACERO: 0.5, FUEGO: 0.5, PLANTA: 0.5, DRAGON: 0.5 },
    ELECTRICO: { VOLADOR: 2, AGUA: 2, PLANTA: 0.5, ELECTRICO: 0.5, DRAGON: 0.5, TIERRA: 0 },
    HIELO:     { VOLADOR: 2, TIERRA: 2, PLANTA: 2, DRAGON: 2, ACERO: 0.5, FUEGO: 0.5, AGUA: 0.5, HIELO: 0.5 },
    LUCHA:     { NORMAL: 2, ROCA: 2, ACERO: 2, HIELO: 2, SINIESTRO: 2, VOLADOR: 0.5, VENENO: 0.5, BICHO: 0.5, PSIQUICO: 0.5, HADA: 0.5, FANTASMA: 0 },
    VENENO:    { PLANTA: 2, HADA: 2, VENENO: 0.5, TIERRA: 0.5, ROCA: 0.5, FANTASMA: 0.5, ACERO: 0 },
    TIERRA:    { VENENO: 2, ROCA: 2, ACERO: 2, FUEGO: 2, ELECTRICO: 2, BICHO: 0.5, PLANTA: 0.5, VOLADOR: 0 },
    VOLADOR:   { LUCHA: 2, BICHO: 2, PLANTA: 2, ROCA: 0.5, ACERO: 0.5, ELECTRICO: 0.5 },
    PSIQUICO:  { LUCHA: 2, VENENO: 2, ACERO: 0.5, PSIQUICO: 0.5, SINIESTRO: 0 },
    BICHO:     { PLANTA: 2, PSIQUICO: 2, SINIESTRO: 2, LUCHA: 0.5, VOLADOR: 0.5, VENENO: 0.5, FANTASMA: 0.5, ACERO: 0.5, FUEGO: 0.5, HADA: 0.5 },
    ROCA:      { VOLADOR: 2, BICHO: 2, FUEGO: 2, HIELO: 2, LUCHA: 0.5, TIERRA: 0.5, ACERO: 0.5 },
    FANTASMA:  { FANTASMA: 2, PSIQUICO: 2, SINIESTRO: 0.5, NORMAL: 0 },
    DRAGON:    { DRAGON: 2, ACERO: 0.5, HADA: 0 },
    SINIESTRO: { FANTASMA: 2, PSIQUICO: 2, LUCHA: 0.5, SINIESTRO: 0.5, HADA: 0.5 },
    ACERO:     { ROCA: 2, HIELO: 2, HADA: 2, ACERO: 0.5, FUEGO: 0.5, AGUA: 0.5, ELECTRICO: 0.5 },
    HADA:      { LUCHA: 2, DRAGON: 2, SINIESTRO: 2, VENENO: 0.5, ACERO: 0.5, FUEGO: 0.5 },
};

// defenderType puede ser un tipo o la lista de tipos del defensor:
// contra doble tipo se multiplican ambos efectos (de ×0 a ×4).
export const getTypeMultiplier = (attackerType, defenderType) => {
    const defenderTypes = Array.isArray(defenderType) ? defenderType : [defenderType];
    return defenderTypes.reduce(
        (multiplier, type) => multiplier * (TYPE_CHART[attackerType]?.[type] ?? 1.0),
        1.0,
    );
};

export const getEffectivenessMessage = (multiplier) => {
    if (multiplier === 0) return 'No afecta...';
    if (multiplier > 1) return '¡Es súper efectivo!';
    if (multiplier < 1) return 'No es muy efectivo...';
    return null;
};

// Traducción del tipo a su nombre visible en español
export const TYPE_LABELS = {
    NORMAL: 'Normal',
    FUEGO: 'Fuego',
    AGUA: 'Agua',
    PLANTA: 'Planta',
    ELECTRICO: 'Eléctrico',
    HIELO: 'Hielo',
    LUCHA: 'Lucha',
    VENENO: 'Veneno',
    TIERRA: 'Tierra',
    VOLADOR: 'Volador',
    PSIQUICO: 'Psíquico',
    BICHO: 'Bicho',
    ROCA: 'Roca',
    FANTASMA: 'Fantasma',
    DRAGON: 'Dragón',
    SINIESTRO: 'Siniestro',
    ACERO: 'Acero',
    HADA: 'Hada',
};
