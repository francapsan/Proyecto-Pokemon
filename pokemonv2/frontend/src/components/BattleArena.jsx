import React, { useState, useEffect, useCallback } from 'react';
import './Battle.css';

// --- SUB-COMPONENTE: Barra de Vida ---
const HealthBar = ({ currentHp, maxHp, name }) => {
    const hpPercentage = (currentHp / maxHp) * 100;
    let barColorClass = 'green';
    if (hpPercentage <= 50) barColorClass = 'yellow';
    if (hpPercentage <= 20) barColorClass = 'red';

    return (
        <div className="health-bar-container">
            <h3 className="pokemon-name">{name}</h3>
            <div className="health-bar">
                <div
                    className={`health-bar-inner ${barColorClass}`}
                    style={{ width: `${hpPercentage}%` }}
                />
            </div>
            <p className="hp-text">{currentHp} / {maxHp}</p>
        </div>
    );
};

// --- SUB-COMPONENTE: Texto con efecto de máquina de escribir ---
const TypewriterText = ({ text, speed = 40 }) => {
    const [displayedText, setDisplayedText] = useState('');

    useEffect(() => {
        if (!text) {
            setDisplayedText('');
            return;
        }

        setDisplayedText(''); // Reinicia el texto al cambiar el prop
        let i = 0;
        const intervalId = setInterval(() => {
            if (i <= text.length) {
                setDisplayedText(text.substring(0, i + 1));
                i++;
            } else {
                clearInterval(intervalId);
            }
        }, speed);

        return () => clearInterval(intervalId);
    }, [text, speed]);

    return <p>{displayedText}</p>;
};

// --- COMPONENTE PRINCIPAL: Arena de Combate ---
const BattleArena = ({ trainers = [], teams = { 1: [], 2: [] }, battleMusic = null }) => {
    // --- ESTADO DE LOS POKÉMON ---
    const [player1Pokemon, setPlayer1Pokemon] = useState({
        name: teams[1]?.[0]?.name || 'Charizard',
        hp: teams[1]?.[0]?.hp || 78,
        maxHp: teams[1]?.[0]?.hp || 78,
        sprite: `/gifs/${(teams[1]?.[0]?.name || 'charizard').toLowerCase()}.gif`,
    });

    const [player2Pokemon, setPlayer2Pokemon] = useState({
        name: teams[2]?.[0]?.name || 'Blastoise',
        hp: teams[2]?.[0]?.hp || 79,
        maxHp: teams[2]?.[0]?.hp || 79,
        sprite: `/gifs/${(teams[2]?.[0]?.name || 'blastoise').toLowerCase()}.gif`,
    });

    // --- ESTADO DE LA INTERFAZ Y ANIMACIONES ---
    const [dialogMessage, setDialogMessage] = useState('¡Que comience la batalla!');
    const [isTurnInProgress, setIsTurnInProgress] = useState(false);
    const [isBattleMusicPlaying, setIsBattleMusicPlaying] = useState(true);

    // Estados para controlar las animaciones CSS
    const [animationState, setAnimationState] = useState({
        attacking: null, // 'player1' o 'player2'
        takingDamage: null, // 'player1' o 'player2'
        projectile: null, // { type, direction }
        arenaEffect: null, // 'earthquake'
    });

    // Función de utilidad para crear pausas
    const delay = (ms) => new Promise(res => setTimeout(res, ms));

    // Función para controlar la música de combate
    const toggleBattleMusic = () => {
        if (battleMusic) {
            if (isBattleMusicPlaying) {
                battleMusic.pause();
                setIsBattleMusicPlaying(false);
            } else {
                battleMusic.play().catch(e => console.error("Error al reproducir música:", e));
                setIsBattleMusicPlaying(true);
            }
        }
    };

    // --- ORQUESTACIÓN DEL TURNO ---
    const executeTurn = useCallback(async (turnData) => {
        if (isTurnInProgress) return;
        setIsTurnInProgress(true);

        const { attacker, defender, attack, effectiveness, damage, defenderHpLeft } = turnData;

        // 1. Anunciar el ataque
        setDialogMessage(`¡${attacker.name} usó ${attack.name}!`);
        await delay(1800);

        // 2. Iniciar animación de ataque (y proyectil si existe)
        setAnimationState(prev => ({
            ...prev,
            attacking: attacker.id,
            projectile: attack.isProjectile ? {
                type: attack.type.toLowerCase(), // 'fire' -> 'flamethrower'
                direction: attacker.id === 'player1' ? 'left-to-right' : 'right-to-left'
            } : null
        }));

        // Esperar a que el proyectil viaje (o la embestida termine)
        await delay(1000);

        // 3. Iniciar animación de daño y actualizar vida
        setAnimationState(prev => ({
            ...prev,
            projectile: null, // Ocultar proyectil al impactar
            takingDamage: defender.id
        }));

        if (defender.id === 'player1') {
            setPlayer1Pokemon(p => ({ ...p, hp: Math.max(0, defenderHpLeft) }));
        } else {
            setPlayer2Pokemon(p => ({ ...p, hp: Math.max(0, defenderHpLeft) }));
        }

        // Esperar a que la animación de daño termine
        await delay(800);

        // 4. Anunciar efectividad (si la hay)
        if (effectiveness) {
            setDialogMessage(effectiveness);
            await delay(1500);
        }

        // 5. Limpiar estados y finalizar turno
        setAnimationState({ attacking: null, takingDamage: null, projectile: null, arenaEffect: null });
        setDialogMessage('Esperando el próximo movimiento...');
        setIsTurnInProgress(false);

    }, [isTurnInProgress]);

    // --- DATOS DE EJEMPLO Y CONTROLES PARA PRUEBAS ---
    const mockTurn1 = {
        attacker: { id: 'player1', name: player1Pokemon.name },
        defender: { id: 'player2', name: player2Pokemon.name },
        attack: { name: 'Lanzallamas', isProjectile: true, type: 'FIRE' },
        effectiveness: '¡No es muy efectivo...',
        damage: 20,
        defenderHpLeft: player2Pokemon.hp - 20,
    };

    const mockTurn2 = {
        attacker: { id: 'player2', name: player2Pokemon.name },
        defender: { id: 'player1', name: player1Pokemon.name },
        attack: { name: 'Hidrobomba', isProjectile: true, type: 'WATER' },
        effectiveness: '¡Es súper efectivo!',
        damage: 40,
        defenderHpLeft: player1Pokemon.hp - 40,
    };

    // Clases CSS dinámicas para la arena y las plataformas
    const arenaClasses = `battle-arena ${animationState.arenaEffect ? `arena-${animationState.arenaEffect}` : ''}`;
    const platform1Classes = `pokemon-platform left ${animationState.attacking === 'player1' ? 'is-attacking-left' : ''} ${animationState.takingDamage === 'player1' ? 'is-taking-damage' : ''}`;
    const platform2Classes = `pokemon-platform right ${animationState.attacking === 'player2' ? 'is-attacking-right' : ''} ${animationState.takingDamage === 'player2' ? 'is-taking-damage' : ''}`;

    // Mapeo de tipos a clases de proyectil
    const projectileClassMap = {
        fire: 'flamethrower',
        water: 'hydro-pump',
        // Añadir más si es necesario
    };

    return (
        <>
            {/* Botón de control de música */}
            <div className="music-toggle" onClick={toggleBattleMusic}>
                {isBattleMusicPlaying ? '🔊' : '🔇'}
            </div>

            {/* Controles de prueba para simular turnos */}
            <div className="battle-controls">
                <button onClick={() => executeTurn(mockTurn1)} disabled={isTurnInProgress}>
                    Turno 1: Charizard ataca
                </button>
                <button onClick={() => executeTurn(mockTurn2)} disabled={isTurnInProgress}>
                    Turno 2: Blastoise ataca
                </button>
                 <button onClick={() => {
                    setPlayer1Pokemon(p => ({ ...p, hp: p.maxHp }));
                    setPlayer2Pokemon(p => ({ ...p, hp: p.maxHp }));
                    setDialogMessage('¡Combate reiniciado!');
                }}>
                    Reiniciar
                </button>
            </div>

            <div className={arenaClasses}>
                {/* --- Recuadros de HP (posicionados arriba) --- */}
                <div className="health-bar-container left">
                    <HealthBar
                        name={player1Pokemon.name}
                        currentHp={player1Pokemon.hp}
                        maxHp={player1Pokemon.maxHp}
                    />
                </div>
                <div className="health-bar-container right">
                    <HealthBar
                        name={player2Pokemon.name}
                        currentHp={player2Pokemon.hp}
                        maxHp={player2Pokemon.maxHp}
                    />
                </div>

                {/* --- Lado del Jugador 1 (Izquierda) --- */}
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', order: 1 }}>
                    <div className={platform1Classes}>
                        <img
                            src={player1Pokemon.sprite}
                            alt={player1Pokemon.name}
                            className="pokemon-sprite"
                        />
                        <div className="pokemon-base" />
                    </div>
                </div>

                {/* --- Proyectil (renderizado condicional) --- */}
                {animationState.projectile && (
                    <div
                        className={`
                            projectile 
                            ${projectileClassMap[animationState.projectile.type] || ''} 
                            ${animationState.projectile.direction}
                        `}
                    />
                )}

                {/* --- Lado del Jugador 2 (Derecha) --- */}
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', order: 3 }}>
                    <div className={platform2Classes}>
                        <img
                            src={player2Pokemon.sprite}
                            alt={player2Pokemon.name}
                            className="pokemon-sprite"
                        />
                        <div className="pokemon-base" />
                    </div>
                </div>

                {/* --- Caja de Diálogo --- */}
                <div className="dialog-box">
                    <TypewriterText text={dialogMessage} />
                </div>
            </div>
        </>
    );
};

export default BattleArena;
