import React, { useState, useEffect, useRef } from 'react';
import './TrainerSetup.css';
import { API_CONFIG, AUDIO_CONFIG, GAME_CONFIG } from '../constants/config';
import PokemonSelection from './PokemonSelection';

const TrainerSetup = () => { 
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [isWaiting, setIsWaiting] = useState(false);
    const [showStartModal, setShowStartModal] = useState(true);
    const [trainers, setTrainers] = useState([]);
    const [isSelectionPhase, setIsSelectionPhase] = useState(false);
    const [name, setName] = useState('');
    const [gender, setGender] = useState('');
    const [message, setMessage] = useState('');
    const [isMusicPlaying, setIsMusicPlaying] = useState(true);

    const backgroundMusic = useRef(null);
    const clickSound = useRef(null);

    useEffect(() => {
        backgroundMusic.current = new Audio(AUDIO_CONFIG.SOUNDS.BACKGROUND_MUSIC);
        backgroundMusic.current.loop = true;
        backgroundMusic.current.volume = AUDIO_CONFIG.VOLUME.BACKGROUND;

        clickSound.current = new Audio(AUDIO_CONFIG.SOUNDS.CLICK_BUTTON);
        clickSound.current.volume = AUDIO_CONFIG.VOLUME.CLICK;
    }, []);

    useEffect(() => {
        if (!showStartModal && backgroundMusic.current && backgroundMusic.current.paused) {
            backgroundMusic.current.play().catch(e => {
                console.error("Error al iniciar la música de fondo:", e);
            });
        }
    }, [showStartModal]);

    useEffect(() => {
        // Limpiar los campos cuando cambia de jugador
        if (currentPlayer === 2) {
            setName('');
            setGender('');
            setMessage('');
            setIsWaiting(false);
        }
    }, [currentPlayer]);

    const handleStartGame = () => {
        playClickSound();
        setShowStartModal(false);
    };

    const playClickSound = () => {
        if (clickSound.current) { 
            clickSound.current.currentTime = 0;
            clickSound.current.play().catch(e => console.error("Error al reproducir el sonido de click:", e));
        }
    };

    const toggleBackgroundMusic = () => {
        if (isMusicPlaying) {
            backgroundMusic.current.pause();
            setIsMusicPlaying(false);
        } else {
            backgroundMusic.current.play()
                .then(() => { 
                    setIsMusicPlaying(true);
                })
                .catch(e => { 
                    console.error("Error al reproducir la música de fondo:", e);
                    setIsMusicPlaying(false);
                });
        }
    };

    const handleStartAdventure = async (e) => {
        e.preventDefault();
        playClickSound();

        if (backgroundMusic.current && backgroundMusic.current.paused && isMusicPlaying) {
            backgroundMusic.current.play().catch(e => console.error("Error al reproducir la música de fondo después de la interacción:", e));
        }

        if (!name.trim()) {
            setMessage("¡Falta información! Introduce un nombre.");
            return;
        }
        if (!gender) {
            setMessage("¡Falta información! Selecciona si eres chico o chica.");
            return;
        }

        setIsWaiting(true);

        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.TRAINERS}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, gender }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Error al crear el entrenador');
            }

            const data = await response.json();
            const newTrainers = [...trainers, data];
            setTrainers(newTrainers);
            
            const welcome = gender === 'chica' ? 'Bienvenida' : 'Bienvenido';

            if (currentPlayer === 1) {
                setMessage(`¡${welcome}, ${data.name}! Preparando Jugador 2...`);
                
                setTimeout(() => {
                    setCurrentPlayer(2);
                }, GAME_CONFIG.PLAYER_SETUP_DELAY);
            } else {
                setMessage(`¡${welcome}, ${data.name}! Ambos jugadores están listos. La elección de los Pokemon dará comienzo...`);
                setTimeout(() => {
                    setIsSelectionPhase(true);
                }, 3000);
            }
        } catch (error) {
            setMessage(`Error: ${error.message}`);
            setIsWaiting(false);
        }
    };

    if (isSelectionPhase) {
        return <PokemonSelection trainers={trainers} backgroundMusic={backgroundMusic.current} isMusicPlaying={isMusicPlaying} setIsMusicPlaying={setIsMusicPlaying} />;
    }

    return (
        <>
            {showStartModal && (
                <div className="start-modal-overlay">
                    <div className="start-modal">
                        <h1>--- COMBATE POKÉMON ---</h1>
                        <p>¡Bienvenidos entrenadores!</p>
                        <button className="start-button" onClick={handleStartGame}>
                            Comenzar
                        </button>
                    </div>
                </div>
            )}
            <div className="music-toggle" onClick={toggleBackgroundMusic}>
                {isMusicPlaying ? '🔊' : '🔇'}
            </div>
            <div className="trainer-setup-container">
                <div className="trainer-setup-card">
                    <h1 className="title">--- COMBATE POKÉMON ---</h1>
                    
                    <h2 className="player-turn-title">
                        Turno del Jugador {currentPlayer}
                    </h2>
                    
                    <div className="input-group">
                        <label htmlFor="name-input">Introduce tu nombre:</label>
                        <input
                            type="text"
                            id="name-input"
                            value={name}
                            onChange={(e) => setName(e.target.value)} 
                            placeholder="Ej. Ash Ketchum" 
                            className="name-input"
                            disabled={isWaiting}
                        />
                    </div>

                    <div className="input-group">
                        <label>¿Eres chico o chica?</label>
                        <div className="gender-buttons">
                            <button
                                className={`gender-btn ${gender === 'chico' ? 'selected' : ''}`} 
                                onClick={() => { setGender('chico'); playClickSound(); }}
                                disabled={isWaiting}
                            >
                                Chico
                            </button>
                            <button
                                className={`gender-btn ${gender === 'chica' ? 'selected' : ''}`} 
                                onClick={() => { setGender('chica'); playClickSound(); }}
                                disabled={isWaiting}
                            >
                                Chica
                            </button>
                        </div>
                    </div>
                    {message && (
                        <p className="message">
                            {message}
                            {isWaiting && <img src="/gifs/carga.gif" alt="Cargando" className="loading-gif" />}
                        </p>
                    )}
                    
                    <button
                        className="start-btn"
                        onClick={handleStartAdventure}
                        disabled={isWaiting}
                    >
                        {currentPlayer === 1 ? 'Registrar Jugador 1' : 'Comenzar Aventura'}
                    </button>
                </div>
                {gender === 'chico' && (
                    <img
                        key="chico"
                        src="/gifs/trainer-boy.gif"
                        alt="Entrenador"
                        className="trainer-gif chico-gif"
                    />
                )}
                {gender === 'chica' && (
                    <img
                        key="chica"
                        src="/gifs/trainer-girl.gif"
                        alt="Entrenadora"
                        className="trainer-gif chica-gif"
                    />
                )}
            </div>
        </>
    );
};

export default TrainerSetup;
