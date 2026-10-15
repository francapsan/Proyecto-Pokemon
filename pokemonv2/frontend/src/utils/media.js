// El grito llega como URL de PokéAPI desde el backend; algunos Pokémon no tienen.
export const playCry = (pokemon, volume = 0.7) => {
    if (!pokemon?.cry) return;
    const audio = new Audio(pokemon.cry);
    audio.volume = volume;
    audio.play().catch((error) => {
        console.warn(`No se pudo reproducir el grito de ${pokemon.name}:`, error);
    });
};
