import React, { useState, useEffect, useRef } from 'react';
import './PokemonSelection.css';
import { API_CONFIG, GAME_CONFIG, AUDIO_CONFIG } from '../constants/config';

// Objeto para traducir los tipos de Pokémon al español
const typeTranslations = {
    FIRE: 'Fuego',
    WATER: 'Agua',
    PLANT: 'Planta',
    ELECTRIC: 'Eléctrico',
    NORMAL: 'Normal',
};
const PokemonSelection = ({ trainers, backgroundMusic: sharedBackgroundMusic, isMusicPlaying: sharedIsMusicPlaying, setIsMusicPlaying: setSharedIsMusicPlaying }) => {
    const [pokemons, setPokemons] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [teams, setTeams] = useState({ 1: [], 2: [] });
    const [message, setMessage] = useState('');
    const [isMusicPlaying, setIsMusicPlaying] = useState(sharedIsMusicPlaying !== undefined ? sharedIsMusicPlaying : true);
    const backgroundMusic = useRef(sharedBackgroundMusic);

    const player1Team = teams[1];
    const player2Team = teams[2];
    const p1IsFull = player1Team.length >= GAME_CONFIG.TEAM_SIZE;
    const p2IsFull = player2Team.length >= GAME_CONFIG.TEAM_SIZE;

    // Determina de quién es el turno: Jugador 1 selecciona primero sus 3, luego Jugador 2
    const whoseTurn = p1IsFull ? 2 : 1;
    const isSelectionOver = p1IsFull && p2IsFull;

    useEffect(() => {
        // Si no hay música compartida, crear una nueva instancia
        if (!backgroundMusic.current) {
            backgroundMusic.current = new Audio(AUDIO_CONFIG.SOUNDS.BACKGROUND_MUSIC);
            backgroundMusic.current.loop = true;
            backgroundMusic.current.volume = AUDIO_CONFIG.VOLUME.BACKGROUND;

            backgroundMusic.current.play().catch(e => {
                console.error("Error al iniciar la música de selección:", e);
                setIsMusicPlaying(false);
            });
        }
        // Si hay música compartida, asegurarse de que está tocando
        else if (isMusicPlaying && backgroundMusic.current.paused) {
            backgroundMusic.current.play().catch(e => {
                console.error("Error al reanudar la música:", e);
            });
        }
    }, [isMusicPlaying]);

    useEffect(() => {
        // De momento, solo estos 3 Pokémon estarán disponibles.
        const availablePokemons = [
            { name: 'Charizard', type: 'FIRE', hp: 78, speed: 100 },
            { name: 'Blastoise', type: 'WATER', hp: 79, speed: 78 },
            { name: 'Venusaur', type: 'PLANT', hp: 80, speed: 80 },
        ];
        setPokemons(availablePokemons);
        setIsLoading(false);
    }, []);

    useEffect(() => {
        if (trainers.length < 2) return;

        const p1Name = trainers[0]?.name || 'Jugador 1';
        const p2Name = trainers[1]?.name || 'Jugador 2';

        if (isSelectionOver) {
            setMessage("¡Todos los equipos están listos! ¡Preparaos para la batalla!");
        } else if (whoseTurn === 1) {
            setMessage(`${p1Name}: Elige tus 3 Pokémon (${player1Team.length}/${GAME_CONFIG.TEAM_SIZE})`);
        } else {
            setMessage(`${p2Name}: Elige tus 3 Pokémon (${player2Team.length}/${GAME_CONFIG.TEAM_SIZE})`);
        }
    }, [whoseTurn, isSelectionOver, player1Team.length, player2Team.length, trainers]);

    const handlePokemonSelect = (pokemon) => {
        if (isSelectionOver) return;

        const currentTeam = teams[whoseTurn];
        if (currentTeam.find(p => p.name === pokemon.name)) return;

        // Reproducir el sonido del Pokémon
        const pokemonSoundName = `Grito_de_${pokemon.name}`;
        const audioElement = new Audio(`/sounds/${pokemonSoundName}.ogg`);
        audioElement.volume = AUDIO_CONFIG.VOLUME.SFX || 0.7;
        audioElement.play().catch(e => {
            console.warn(`No se pudo reproducir el sonido para ${pokemon.name}:`, e);
        });

        setTeams(prevTeams => ({
            ...prevTeams,
            [whoseTurn]: [...prevTeams[whoseTurn], pokemon]
        }));
    };

    const toggleBackgroundMusic = () => {
        const setState = setSharedIsMusicPlaying || setIsMusicPlaying;
        
        if (isMusicPlaying) {
            backgroundMusic.current.pause();
            setIsMusicPlaying(false);
            setState(false);
        } else {
            backgroundMusic.current.play()
                .then(() => { 
                    setIsMusicPlaying(true);
                    setState(true);
                })
                .catch(e => { 
                    console.error("Error al reproducir la música de fondo:", e);
                    setIsMusicPlaying(false);
                    setState(false);
                });
        }
    };

    const renderTeam = (team, trainer) => (
        <div className="team-display">
            <h3>Equipo de {trainer?.name}</h3>
            <div className="team-pokemons">
                {Array(GAME_CONFIG.TEAM_SIZE).fill(null).map((_, index) => {
                    const pokemon = team[index];
                    return (
                        <div key={index} className="team-slot">
                            {pokemon ? (
                                <img src={`/gifs/${pokemon.name.toLowerCase()}.gif`} alt={pokemon.name} title={pokemon.name} />
                            ) : (
                                <div className="empty-slot" />
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );

    if (isLoading) {
        return (
            <div className="pokemon-selection-container">
                <div className="loading-container">
                    <p className="loading-text">Cargando Pokémon... <img src="/gifs/carga.gif" alt="Cargando" className="loading-gif" /></p>
                </div>
            </div>
        );
    }

    return (
        <div className="pokemon-selection-container">
            <div className="music-toggle" onClick={toggleBackgroundMusic}>
                {isMusicPlaying ? '🔊' : '🔇'}
            </div>
            <h1 className="title">--- SELECCIÓN DE POKÉMON ---</h1>
            {error && <p className="error-message">{error}</p>}
            <p className="message">{message}</p>

            <div className="teams-container">
                {renderTeam(player1Team, trainers[0])}
                {renderTeam(player2Team, trainers[1])}
            </div>

            <div className="pokemon-grid">
                {pokemons.map(pokemon => {
                    const isAlreadyInP1Team = !!player1Team.find(p => p.name === pokemon.name);
                    const isAlreadyInP2Team = !!player2Team.find(p => p.name === pokemon.name);

                    let cardClass = 'pokemon-card';
                    let isDisabled = false;

                    if (isSelectionOver) {
                        isDisabled = true;
                    } else if (whoseTurn === 1) {
                        if (isAlreadyInP1Team) isDisabled = true;
                    } else { // whoseTurn === 2
                        if (isAlreadyInP2Team) isDisabled = true;
                    }

                    if (isDisabled) cardClass += ' disabled';
                    if (isAlreadyInP1Team) cardClass += ' selected-p1';
                    if (isAlreadyInP2Team) cardClass += ' selected-p2';

                    return (
                        <div 
                            key={pokemon.name} 
                            className={cardClass}
                            onClick={() => !isDisabled && handlePokemonSelect(pokemon)}
                        >
                            <img src={`/gifs/${pokemon.name.toLowerCase()}.gif`} alt={pokemon.name} />
                            <h4>{pokemon.name}</h4>
                            <p>Tipo: {typeTranslations[pokemon.type] || pokemon.type}</p>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default PokemonSelection;