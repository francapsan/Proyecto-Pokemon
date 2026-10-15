import React, { useState, useEffect, useRef } from 'react';
import './PokemonSelection.css';
import { GAME_CONFIG, AUDIO_CONFIG } from '../constants/config';
import { pokemonService } from '../services/pokemonService';
import { TYPE_LABELS } from '../constants/typeChart';
import { playCry } from '../utils/media';
import { splitChoices } from '../utils/pokemonChoices';
import BattleArena from './BattleArena';
import PageShell from './ui/PageShell';
import Panel from './ui/Panel';
import Button from './ui/Button';

// Normaliza la respuesta del backend (`maxHp`) al formato interno (`hp`)
// que ya usan el resto de componentes.
const normalizePokemon = (p) => ({
    name: p.name,
    types: p.types ?? [],
    hp: p.maxHp ?? p.hp,
    attack: p.attack,
    defense: p.defense,
    specialAttack: p.specialAttack,
    specialDefense: p.specialDefense,
    speed: p.speed,
    attacks: p.attacks ?? [],
    sprite: p.sprite ?? null,
    cry: p.cry ?? null,
});
const PokemonSelection = ({ trainers, backgroundMusicRef, isMusicPlaying: sharedIsMusicPlaying, setIsMusicPlaying: setSharedIsMusicPlaying }) => {
    const [choices, setChoices] = useState({ 1: [], 2: [] });
    const [catalog, setCatalog] = useState({});
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [loadAttempt, setLoadAttempt] = useState(0);
    
    const [teams, setTeams] = useState({ 1: [], 2: [] });
    const [isMusicPlaying, setIsMusicPlaying] = useState(sharedIsMusicPlaying !== undefined ? sharedIsMusicPlaying : true);    
    const [showTransition, setShowTransition] = useState(false);
    const [showBattle, setShowBattle] = useState(false);
    // La música de fondo la crea TrainerSetup; si se monta sin ella, se crea aquí.
    const ownBackgroundMusic = useRef(null);
    const backgroundMusic = backgroundMusicRef ?? ownBackgroundMusic;
    const battleMusic = useRef(null);
    const transitionVideoRef = useRef(null);
    // Flags para garantizar que cada paso de la transición solo se ejecuta
    // una vez, aunque el componente siga montado tras renderizar <BattleArena />.
    const transitionStartedRef = useRef(false);
    const battleMusicStartedRef = useRef(false);
    const transitionFinishedRef = useRef(false);

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
    }, [isMusicPlaying, backgroundMusic]);

    useEffect(() => {
        let cancelled = false;

        const applyCatalog = (list) => {
            if (cancelled) return;
            const normalized = list.map(normalizePokemon);
            setChoices(splitChoices(normalized, GAME_CONFIG.POKEMON_CHOICES_PER_PLAYER));
            const byName = Object.fromEntries(normalized.map((p) => [p.name, p]));
            setCatalog(byName);
            setIsLoading(false);
        };

        // Una sola petición con el doble de Pokémon: así los dos jugadores no comparten opciones.
        pokemonService.listRandomPokemons(GAME_CONFIG.POKEMON_CHOICES_PER_PLAYER * 2)
            .then((data) => applyCatalog(data))
            .catch((err) => {
                if (cancelled) return;
                console.error('No se pudieron cargar los Pokémon:', err.message);
                setError('No se pudieron cargar los Pokémon. Comprueba que el servidor está en marcha y vuelve a intentarlo.');
                setIsLoading(false);
            });

        return () => { cancelled = true; };
    }, [loadAttempt]);

    const retryLoad = () => {
        setError(null);
        setIsLoading(true);
        setLoadAttempt((attempt) => attempt + 1);
    };

    const turnMessage = () => {
        if (trainers.length < 2) return '';
        if (isSelectionOver) return '¡Todos los equipos están listos! ¡Preparaos para la batalla!';
        const name = trainers[whoseTurn - 1]?.name || `Jugador ${whoseTurn}`;
        return `${name}: Elige tus 3 Pokémon (${teams[whoseTurn].length}/${GAME_CONFIG.TEAM_SIZE})`;
    };

    // Efecto para iniciar la transición cuando se completa la selección
    // (solo una vez, aunque el componente se re-renderice)
    useEffect(() => {
        if (isSelectionOver && !transitionStartedRef.current) {
            transitionStartedRef.current = true;
            console.log('✅ Selección completa - esperando 3 segundos...');
            const initialWaitTimer = setTimeout(() => {
                console.log('✅ Mostrando vídeo de entrada...');
                // El .mp4 tiene su propio audio: pausamos la música de fondo. La de combate
                // no arranca hasta handleTransitionEnd para que no se solapen.
                if (backgroundMusic.current && !backgroundMusic.current.paused) {
                    backgroundMusic.current.pause();
                    setIsMusicPlaying(false);
                    setSharedIsMusicPlaying?.(false);
                }
                setShowTransition(true);
            }, 3000);

            return () => clearTimeout(initialWaitTimer);
        }
    }, [isSelectionOver, backgroundMusic, setSharedIsMusicPlaying]);

    // Se dispara cuando termina el vídeo de entrada (o el fallback de seguridad).
    // Arranca la música de combate y muestra la arena.
    const handleTransitionEnd = () => {
        if (transitionFinishedRef.current) return;
        transitionFinishedRef.current = true;

        if (!battleMusicStartedRef.current) {
            battleMusicStartedRef.current = true;
            if (!battleMusic.current) {
                battleMusic.current = new Audio(AUDIO_CONFIG.SOUNDS.BATTLE_MUSIC);
                battleMusic.current.volume = AUDIO_CONFIG.VOLUME.BACKGROUND;
                battleMusic.current.loop = true;
            }
            battleMusic.current.play().catch(e => {
                console.error("Error al reproducir la música de combate:", e);
            });
        }

        console.log('✅ Vídeo de entrada terminado - mostrando BattleArena...');
        setShowTransition(false);
        setShowBattle(true);
    };

    const handlePokemonSelect = (pokemon) => {
        if (isSelectionOver) return;

        const currentTeam = teams[whoseTurn];
        if (currentTeam.find(p => p.name === pokemon.name)) return;

        playCry(pokemon, AUDIO_CONFIG.VOLUME.SFX || 0.7);

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
        <Panel variant="light" padding="sm" className="team-display">
            <h3>Equipo de {trainer?.name}</h3>
            <div className="team-pokemons">
                {Array(GAME_CONFIG.TEAM_SIZE).fill(null).map((_, index) => {
                    const pokemon = team[index];
                    return (
                        <div key={index} className="team-slot">
                            {pokemon ? (
                                <img src={pokemon.sprite} alt={pokemon.name} title={pokemon.name} />
                            ) : (
                                <div className="empty-slot" />
                            )}
                        </div>
                    );
                })}
            </div>
        </Panel>
    );

    if (isLoading) {
        return (
            <PageShell
                currentStep="selection"
                isMusicPlaying={isMusicPlaying}
                onToggleMusic={toggleBackgroundMusic}
                chrome="hidden"
            >
                <div className="pokemon-selection-container">
                    <div className="loading-container">
                        <p className="loading-text">
                            Cargando Pokémon...{' '}
                            <img src="/media/carga.gif" alt="Cargando" className="loading-gif" />
                        </p>
                    </div>
                </div>
            </PageShell>
        );
    }

    if (error) {
        return (
            <PageShell
                currentStep="selection"
                isMusicPlaying={isMusicPlaying}
                onToggleMusic={toggleBackgroundMusic}
            >
                <div className="pokemon-selection-container">
                    <div className="loading-container">
                        <p className="error-message">{error}</p>
                        <Button variant="primary" size="lg" onClick={retryLoad}>
                            Reintentar
                        </Button>
                    </div>
                </div>
            </PageShell>
        );
    }

    if (showBattle) {
        console.log('🎮 BattleArena renderizado con:', { trainers, teams });
        return (
            <BattleArena 
                trainers={trainers} 
                teams={teams} 
                battleMusicRef={battleMusic} 
                catalog={catalog}
            />
        );
    }

    return (
        <PageShell
            currentStep="selection"
            isMusicPlaying={isMusicPlaying}
            onToggleMusic={toggleBackgroundMusic}
            chrome={showTransition ? 'hidden' : 'full'}
        >
            <div className="pokemon-selection-container">
                {showTransition && (
                    <div className="transition-overlay">
                        <video
                            ref={transitionVideoRef}
                            src="/media/entrada.mp4"
                            className="transition-video"
                            autoPlay
                            playsInline
                            onEnded={handleTransitionEnd}
                            onError={(e) => {
                                console.error('Error reproduciendo el vídeo de entrada:', e);
                                handleTransitionEnd();
                            }}
                        />
                    </div>
                )}
                <h2 className="pokemon-selection-heading">Elige tu equipo</h2>
                <p className="message">{turnMessage()}</p>

            <div className="teams-container">
                {renderTeam(player1Team, trainers[0])}
                {renderTeam(player2Team, trainers[1])}
            </div>

            <div className="pokemon-grid">
                {choices[whoseTurn].map(pokemon => {
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
                            <div className="pokemon-card-sprite">
                                <img src={pokemon.sprite} alt={pokemon.name} />
                            </div>
                            <h4>{pokemon.name}</h4>
                            <p>Tipo: {pokemon.types.map((type) => TYPE_LABELS[type] || type).join(' / ')}</p>
                        </div>
                    );
                })}
                </div>
            </div>
        </PageShell>
    );
};

export default PokemonSelection;