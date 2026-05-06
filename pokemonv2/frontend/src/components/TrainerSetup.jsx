import React, { useState, useEffect, useRef } from 'react';
import './TrainerSetup.css';

const TrainerSetup = () => { 
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [isWaiting, setIsWaiting] = useState(false);
    const [showStartModal, setShowStartModal] = useState(true);
    const [name, setName] = useState('');
    const [gender, setGender] = useState('');
    const [message, setMessage] = useState('');
    const [isMusicPlaying, setIsMusicPlaying] = useState(true);
    const backgroundMusic = useRef(null);
    const clickSound = useRef(null);

    useEffect(() => {
        backgroundMusic.current = new Audio('/sounds/Theme.mp3');
        backgroundMusic.current.loop = true;
        backgroundMusic.current.volume = 0.3;

        clickSound.current = new Audio('/sounds/Click Button.mp3');
        clickSound.current.volume = 0.7;
    }, []);

    useEffect(() => {
        if (!showStartModal && backgroundMusic.current && backgroundMusic.current.paused) {
            backgroundMusic.current.play().catch(e => {
                console.error("Error al iniciar la música de fondo:", e);
            });
        }
    }, [showStartModal]);

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
            const response = await fetch('http://localhost:8080/api/trainers', {
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
            
            if (currentPlayer === 1) {
                setMessage(`¡Bienvenid@, ${data.name}! Preparando Jugador 2...`);
                
                setTimeout(() => {
                    setCurrentPlayer(2);
                }, 4000);
            } else {
                setMessage(`¡Bienvenid@, ${data.name}! Ambos jugadores están listos. La elección de Pokemons dará comienzo...`);
                setIsWaiting(false); 
                if (backgroundMusic.current) {
                    backgroundMusic.current.pause(); 
                    backgroundMusic.current.currentTime = 0;
                    setIsMusicPlaying(false);
                }
            }
        } catch (error) {
            setMessage(`Error: ${error.message}`);
            setIsWaiting(false);
        }
    };

    return (
        <>
            {showStartModal && (
                <div className="start-modal-overlay">
                    <div className="start-modal">
                        <h1>--- CAMPEONATO POKÉMON ---</h1>
                        <p>¡Bienvenido entrenador!</p>
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
                    <h1 className="title">--- CAMPEONATO POKÉMON ---</h1>
                    
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
                    {message && <p className="message">{message}</p>}
                    
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
