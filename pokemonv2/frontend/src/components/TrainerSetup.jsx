import React, { useState, useEffect, useRef } from 'react';
import './TrainerSetup.css';

const TrainerSetup = () => {
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [isWaiting, setIsWaiting] = useState(false);
    
    const [isMusicPlaying, setIsMusicPlaying] = useState(true); // Estado para controlar si la música de fondo está sonando
    const [showStartModal, setShowStartModal] = useState(true); // Modal de inicio
    const [name, setName] = useState('');
    const [gender, setGender] = useState('');
    const [message, setMessage] = useState('');

    // Refs para los elementos de audio
    const backgroundMusic = useRef(null);
    const clickSound = useRef(null);

    useEffect(() => {
        // Inicializar música de fondo
        backgroundMusic.current = new Audio('/sounds/Theme.mp3'); // Actualizado al nombre de tu archivo
        backgroundMusic.current.loop = true;
        backgroundMusic.current.volume = 0.3; // Ajusta el volumen a tu gusto

        // Inicializar sonido de click
        clickSound.current = new Audio('/sounds/Click Button.mp3'); // Actualizado al nombre de tu archivo
        clickSound.current.volume = 0.7; // Ajusta el volumen a tu gusto

        // No reproducir automáticamente, esperar a que el usuario presione el modal
    }, []);

    // Efecto para reproducir música cuando se cierre el modal
    useEffect(() => {
        if (!showStartModal && backgroundMusic.current && backgroundMusic.current.paused) {
            backgroundMusic.current.play().catch(e => {
                console.warn("Error al reproducir música:", e);
            });
        }
    }, [showStartModal]);

    const handleStartGame = () => {
        playClickSound();
        setShowStartModal(false);
    };

    const playClickSound = () => {
        if (clickSound.current) {
            clickSound.current.currentTime = 0; // Reiniciar el sonido para que siempre empiece desde el principio
            clickSound.current.play().catch(e => console.error("Error al reproducir el sonido de click:", e));
        }
    };

    const toggleBackgroundMusic = () => {
        if (!backgroundMusic.current) return;

        if (isMusicPlaying) {
            backgroundMusic.current.pause();
            setIsMusicPlaying(false);
        } else {
            backgroundMusic.current.play()
                .then(() => {
                    setIsMusicPlaying(true); // Actualizar solo si la reproducción es exitosa
                })
                .catch(e => {
                    console.error("Error al reproducir la música de fondo:", e);
                    setIsMusicPlaying(false); // Si falla al intentar reproducir, se mantiene como no sonando
                });
        }
    };

    const handleStartAdventure = async (e) => {
        e.preventDefault();
        playClickSound(); // Reproducir sonido de click al enviar el formulario

        // Si la música de fondo fue bloqueada Y el usuario quiere que suene, intentar reproducirla ahora que hay interacción del usuario
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
            const response = await fetch('http://localhost:8080/api/trainers/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, gender }),
            });

            if (response.ok) {
                const data = await response.json();
                
                if (currentPlayer === 1) {
                    setMessage(`¡Bienvenid@, ${data.name}! Preparando Jugador 2...`);
                    
                    setTimeout(() => {
                        setName('');
                        setGender('');
                        setMessage('');
                        setCurrentPlayer(2);
                        setIsWaiting(false);
                    }, 4000);
                } else {
                    setMessage(`¡Bienvenid@, ${data.name}! Ambos jugadores están listos. La elección de Pokemons dará comienzo...`);
                    setIsWaiting(false);
                    // Opcional: pausar la música de fondo cuando ambos jugadores estén listos y la fase cambie
                    if (backgroundMusic.current) {
                        backgroundMusic.current.pause();
                        backgroundMusic.current.currentTime = 0;
                        setIsMusicPlaying(false); // Actualizar el estado de la música
                    }
                }
            } else {
                const errorText = await response.text();
                setMessage(`Error: ${errorText}`);
                setIsWaiting(false);
            }
        } catch (error) {
            setMessage("Error de conexión con el servidor. ¿Está encendido Spring Boot?");
            setIsWaiting(false);
        }
    };

    return (
        <> {/* Usar un fragmento para envolver múltiples elementos */}
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
                {isMusicPlaying ? '🔊' : '🔇'} {/* Iconos Unicode */}
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
                            onClick={() => { setGender('chico'); playClickSound(); }} // Añadido sonido de click
                            disabled={isWaiting}
                        >
                            Chico
                        </button>
                        <button
                            className={`gender-btn ${gender === 'chica' ? 'selected' : ''}`} 
                            onClick={() => { setGender('chica'); playClickSound(); }} // Añadido sonido de click
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
            </div> {/* Cierre de trainer-setup-card */}
            </div> {/* Cierre de trainer-setup-container */}
        </>
    );
};

export default TrainerSetup;
