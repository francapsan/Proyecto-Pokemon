import React, { useState } from 'react';
import './TrainerSetup.css';

const TrainerSetup = () => {
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [isWaiting, setIsWaiting] = useState(false);
    
    const [name, setName] = useState('');
    const [gender, setGender] = useState('');
    const [message, setMessage] = useState('');

    const handleStartAdventure = async (e) => {
        e.preventDefault();
        
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
                    setMessage(`¡Bienvenid@, ${data.name}! Ambos jugadores están listos. La elección de Pokemons dara comienzo...`);
                    setIsWaiting(false);
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
        <div className="trainer-setup-container">
            <div className="trainer-setup-card">
                <h1 className="title">--- CAMPEONATO POKÉMON ---</h1>
                
                <h2 style={{ color: '#3b4cca', marginBottom: '1.5rem' }}>
                    Turno del Jugador {currentPlayer}
                </h2>
                
                <div className="input-group">
                    <label>Introduce tu nombre:</label>
                    <input 
                        type="text" 
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
                            onClick={() => setGender('chico')}
                            disabled={isWaiting}
                        >
                            Chico
                        </button>
                        <button 
                            className={`gender-btn ${gender === 'chica' ? 'selected' : ''}`} 
                            onClick={() => setGender('chica')}
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
        </div>
    );
};

export default TrainerSetup;
