import React, { useState, useEffect, useCallback } from 'react';
import './Battle.css';
import { AUDIO_CONFIG } from '../constants/config';
import { POKEMON_CATALOG } from '../constants/pokemons';
import { getTypeMultiplier, getEffectivenessMessage, POKEMON_TYPES } from '../constants/typeChart';

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

// --- SUB-COMPONENTE: Cuadro pequeño con los Pokémon vivos del equipo ---
const AliveIndicatorBox = ({ aliveCount }) => {
    if (aliveCount <= 0) return null;
    return (
        <div
            className="alive-indicator-box"
            aria-label={`Pokémon disponibles: ${aliveCount}`}
        >
            <div className="alive-indicator">
                {Array.from({ length: aliveCount }).map((_, idx) => (
                    <img
                        key={idx}
                        src="/gifs/espera.gif"
                        alt=""
                        className="alive-indicator-icon"
                    />
                ))}
            </div>
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

        setDisplayedText('');
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

// Construye el estado inicial de un Pokémon a partir de su nombre,
// resolviendo type, speed, ataques y sprite desde el catálogo.
const buildPokemonState = (basePokemon, fallbackName) => {
    const data = POKEMON_CATALOG[basePokemon?.name] || POKEMON_CATALOG[fallbackName];
    return {
        name: data.name,
        type: data.type,
        speed: data.speed,
        attacks: data.attacks,
        hp: data.hp,
        maxHp: data.hp,
        sprite: `/gifs/${data.name.toLowerCase()}.gif`,
    };
};

// Construye un equipo completo (array de estados de Pokémon).
// Se usa para mantener el HP residual de cada Pokémon entre cambios.
const buildTeam = (teamArr, fallbackName) => {
    const source = (Array.isArray(teamArr) && teamArr.length > 0)
        ? teamArr
        : [{ name: fallbackName }];
    return source.map(p => buildPokemonState(p, fallbackName));
};

// Mapeo de tipo del ataque a clase CSS del proyectil.
// Los ataques NORMAL son cuerpo a cuerpo (sin proyectil).
const PROJECTILE_TYPE_TO_CLASS = {
    [POKEMON_TYPES.FUEGO]: 'fire',
    [POKEMON_TYPES.AGUA]: 'water',
    [POKEMON_TYPES.PLANTA]: 'plant',
};

// --- COMPONENTE PRINCIPAL: Arena de Combate ---
const BattleArena = ({ trainers = [], teams = { 1: [], 2: [] }, battleMusic = null }) => {
    // --- EQUIPOS COMPLETOS (mantienen HP residual de cada Pokémon) ---
    const [player1Team, setPlayer1Team] = useState(() => buildTeam(teams[1], 'Charizard'));
    const [player2Team, setPlayer2Team] = useState(() => buildTeam(teams[2], 'Blastoise'));

    // --- ÍNDICE DEL POKÉMON ACTIVO DE CADA EQUIPO ---
    const [player1Index, setPlayer1Index] = useState(0);
    const [player2Index, setPlayer2Index] = useState(0);

    // Activos derivados del equipo + índice
    const player1Pokemon = player1Team[player1Index];
    const player2Pokemon = player2Team[player2Index];

    // --- TURNO ACTIVO: empieza el más rápido (a igual velocidad, jugador 1) ---
    const [currentTurn, setCurrentTurn] = useState(() => {
        const p1 = buildPokemonState(teams[1]?.[0], 'Charizard');
        const p2 = buildPokemonState(teams[2]?.[0], 'Blastoise');
        return p1.speed >= p2.speed ? 'player1' : 'player2';
    });

    // --- ESTADO DE LA INTERFAZ Y ANIMACIONES ---
    const [dialogMessage, setDialogMessage] = useState('¡Que comience la batalla!');
    const [isTurnInProgress, setIsTurnInProgress] = useState(false);
    const [isBattleMusicPlaying, setIsBattleMusicPlaying] = useState(true);
    const [battleOver, setBattleOver] = useState(false);
    // Cuando es true, el panel muestra la lista de Pokémon disponibles para cambiar
    const [isSwitching, setIsSwitching] = useState(false);

    const [animationState, setAnimationState] = useState({
        attacking: null,
        takingDamage: null,
        projectile: null,
        arenaEffect: null,
        fainting: null,
    });

    const delay = (ms) => new Promise(res => setTimeout(res, ms));

    // Reproduce el grito de un Pokémon
    const playPokemonCry = (pokemonName) => {
        if (!pokemonName) return;
        try {
            const cry = new Audio(`/sounds/Grito_de_${pokemonName}.ogg`);
            cry.volume = AUDIO_CONFIG.VOLUME?.SFX ?? 0.7;
            cry.play().catch(e => {
                console.warn(`No se pudo reproducir el grito de ${pokemonName}:`, e);
            });
        } catch (e) {
            console.warn(`Error inicializando el grito de ${pokemonName}:`, e);
        }
    };

    const getTrainerName = (playerId) => {
        const idx = playerId === 'player1' ? 0 : 1;
        return trainers[idx]?.name || `Jugador ${idx + 1}`;
    };

    // Mensaje "¿Qué hará X?" cuando empieza un turno
    useEffect(() => {
        if (!battleOver && !isTurnInProgress && !isSwitching) {
            setDialogMessage(`¿Qué hará ${getTrainerName(currentTurn)}?`);
        }
        // Dependencias intencionalmente acotadas.
        // No queremos pisar el diálogo durante una animación.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentTurn, battleOver]);

    // Música de combate
    const toggleBattleMusic = () => {
        if (!battleMusic) return;
        if (isBattleMusicPlaying) {
            battleMusic.pause();
            setIsBattleMusicPlaying(false);
        } else {
            battleMusic.play().catch(e => console.error("Error al reproducir música:", e));
            setIsBattleMusicPlaying(true);
        }
    };

    // Helpers para actualizar el HP de un Pokémon concreto del equipo
    const updateTeamHp = (playerId, index, newHp) => {
        const setter = playerId === 'player1' ? setPlayer1Team : setPlayer2Team;
        setter(prev => prev.map((p, i) => (i === index ? { ...p, hp: newHp } : p)));
    };

    // --- GESTIÓN DEL POKÉMON DEBILITADO ---
    // Devuelve true si la batalla terminó (no quedan más Pokémon vivos).
    const handleFaint = useCallback(async (defenderId) => {
        const isP1 = defenderId === 'player1';
        const team = isP1 ? player1Team : player2Team;
        const currentIndex = isP1 ? player1Index : player2Index;
        const defenderPokemon = team[currentIndex];
        const ownerName = getTrainerName(defenderId);

        // 1. Sonido + mensaje + animación de fainting
        playPokemonCry(defenderPokemon.name);
        setDialogMessage(`¡${defenderPokemon.name} se ha debilitado!`);
        setAnimationState({
            attacking: null,
            takingDamage: null,
            projectile: null,
            arenaEffect: null,
            fainting: defenderId,
        });
        await delay(1600);

        // 2. Buscar siguiente Pokémon vivo (puede no ser el inmediatamente siguiente
        // si el jugador ya había hecho cambios previamente)
        const nextIndex = team.findIndex((p, i) => i !== currentIndex && p.hp > 0);
        if (nextIndex === -1) {
            const winnerName = getTrainerName(isP1 ? 'player2' : 'player1');
            setDialogMessage(`¡${winnerName} ha ganado la batalla!`);
            setBattleOver(true);
            return true;
        }

        // 3. Cargar el siguiente Pokémon disponible
        const nextPokemon = team[nextIndex];
        if (isP1) {
            setPlayer1Index(nextIndex);
        } else {
            setPlayer2Index(nextIndex);
        }

        setAnimationState({
            attacking: null,
            takingDamage: null,
            projectile: null,
            arenaEffect: null,
            fainting: null,
        });
        setDialogMessage(`¡${ownerName} envía a ${nextPokemon.name}!`);
        await delay(1500);
        return false;
    }, [player1Team, player2Team, player1Index, player2Index, trainers]);

    // --- EJECUCIÓN DE UN ATAQUE ELEGIDO POR EL JUGADOR ACTIVO ---
    const executeAttack = useCallback(async (attack) => {
        if (isTurnInProgress || battleOver) return;
        setIsTurnInProgress(true);

        const isP1Attacking = currentTurn === 'player1';
        const attacker = isP1Attacking ? player1Pokemon : player2Pokemon;
        const defender = isP1Attacking ? player2Pokemon : player1Pokemon;
        const attackerId = currentTurn;
        const defenderId = isP1Attacking ? 'player2' : 'player1';
        const defenderIndex = isP1Attacking ? player2Index : player1Index;

        // Cálculo de daño según la tabla de tipos
        const multiplier = getTypeMultiplier(attack.type, defender.type);
        const finalDamage = Math.floor(attack.damage * multiplier);
        const newHp = Math.max(0, defender.hp - finalDamage);
        const effectivenessMsg = getEffectivenessMessage(multiplier);

        // 1. Anunciar el ataque
        setDialogMessage(`¡${attacker.name} usó ${attack.name}!`);
        await delay(1500);

        // 2. Animación de ataque (+ proyectil si aplica)
        const projectileClass = PROJECTILE_TYPE_TO_CLASS[attack.type] || null;
        setAnimationState(prev => ({
            ...prev,
            attacking: attackerId,
            projectile: projectileClass ? {
                type: projectileClass,
                direction: attackerId === 'player1' ? 'left-to-right' : 'right-to-left',
            } : null,
        }));
        await delay(1000);

        // 3. Aplicar daño y animar al defensor
        setAnimationState(prev => ({
            ...prev,
            projectile: null,
            takingDamage: defenderId,
        }));
        updateTeamHp(defenderId, defenderIndex, newHp);
        await delay(800);

        // 4. Mensaje de efectividad
        if (effectivenessMsg) {
            setDialogMessage(effectivenessMsg);
            await delay(1500);
        }

        // 5. ¿KO?
        if (newHp <= 0) {
            const finished = await handleFaint(defenderId);
            if (!finished) {
                setCurrentTurn(defenderId);
            }
        } else {
            setAnimationState({
                attacking: null,
                takingDamage: null,
                projectile: null,
                arenaEffect: null,
                fainting: null,
            });
            setCurrentTurn(defenderId);
        }

        setIsTurnInProgress(false);
    }, [isTurnInProgress, battleOver, currentTurn, player1Pokemon, player2Pokemon, player1Index, player2Index, handleFaint]);

    // --- CAMBIO DE POKÉMON (consume el turno) ---
    const handleSwitchClick = () => {
        if (isTurnInProgress || battleOver) return;
        setIsSwitching(true);
        setDialogMessage(`¿A qué Pokémon cambiará ${getTrainerName(currentTurn)}?`);
    };

    const handleSwitchCancel = () => {
        setIsSwitching(false);
        setDialogMessage(`¿Qué hará ${getTrainerName(currentTurn)}?`);
    };

    const performSwitch = useCallback(async (targetIndex) => {
        if (isTurnInProgress || battleOver) return;

        const isP1 = currentTurn === 'player1';
        const team = isP1 ? player1Team : player2Team;
        const currentIndex = isP1 ? player1Index : player2Index;

        if (targetIndex === currentIndex) return;
        const targetPokemon = team[targetIndex];
        if (!targetPokemon || targetPokemon.hp <= 0) return;

        setIsSwitching(false);
        setIsTurnInProgress(true);

        const outgoingPokemon = team[currentIndex];
        const ownerName = getTrainerName(currentTurn);

        // 1. Mensaje de retirada
        setDialogMessage(`¡${ownerName} retira a ${outgoingPokemon.name}!`);
        setAnimationState({
            attacking: null,
            takingDamage: null,
            projectile: null,
            arenaEffect: null,
            fainting: currentTurn,
        });
        await delay(1200);

        // 2. Actualizar índice activo (el equipo conserva el HP residual)
        if (isP1) {
            setPlayer1Index(targetIndex);
        } else {
            setPlayer2Index(targetIndex);
        }

        // 3. Anunciar al nuevo Pokémon
        setAnimationState({
            attacking: null,
            takingDamage: null,
            projectile: null,
            arenaEffect: null,
            fainting: null,
        });
        playPokemonCry(targetPokemon.name);
        setDialogMessage(`¡Adelante, ${targetPokemon.name}!`);
        await delay(1500);

        // 4. El cambio consume el turno → pasa al rival
        setCurrentTurn(isP1 ? 'player2' : 'player1');
        setIsTurnInProgress(false);
    }, [isTurnInProgress, battleOver, currentTurn, player1Team, player2Team, player1Index, player2Index, trainers]);

    // --- CLASES DINÁMICAS ---
    const arenaClasses = `battle-arena ${animationState.arenaEffect ? `arena-${animationState.arenaEffect}` : ''}`;
    const platform1Classes = `pokemon-platform left ${animationState.attacking === 'player1' ? 'is-attacking-left' : ''} ${animationState.takingDamage === 'player1' ? 'is-taking-damage' : ''} ${animationState.fainting === 'player1' ? 'is-fainting' : ''}`;
    const platform2Classes = `pokemon-platform right ${animationState.attacking === 'player2' ? 'is-attacking-right' : ''} ${animationState.takingDamage === 'player2' ? 'is-taking-damage' : ''} ${animationState.fainting === 'player2' ? 'is-fainting' : ''}`;

    const activeTeam = currentTurn === 'player1' ? player1Team : player2Team;
    const activeIndex = currentTurn === 'player1' ? player1Index : player2Index;
    const activeAttacker = activeTeam[activeIndex];

    // Pokémon disponibles para cambiar: distintos del activo y con HP > 0
    const switchableOptions = activeTeam
        .map((p, idx) => ({ pokemon: p, index: idx }))
        .filter(opt => opt.index !== activeIndex && opt.pokemon.hp > 0);

    const canSwitch = switchableOptions.length > 0;
    const showAttackPanel = !battleOver && !isTurnInProgress;

    return (
        <>
            {/* Botón de control de música */}
            <div className="music-toggle" onClick={toggleBattleMusic}>
                {isBattleMusicPlaying ? '🔊' : '🔇'}
            </div>

            <div className={arenaClasses}>
                {/* Recuadros de HP del Jugador 1 */}
                <div className="health-bar-wrapper left">
                    <AliveIndicatorBox aliveCount={player1Team.filter(p => p.hp > 0).length} />
                    <HealthBar
                        name={player1Pokemon.name}
                        currentHp={player1Pokemon.hp}
                        maxHp={player1Pokemon.maxHp}
                    />
                </div>

                {/* Recuadros de HP del Jugador 2 */}
                <div className="health-bar-wrapper right">
                    <AliveIndicatorBox aliveCount={player2Team.filter(p => p.hp > 0).length} />
                    <HealthBar
                        name={player2Pokemon.name}
                        currentHp={player2Pokemon.hp}
                        maxHp={player2Pokemon.maxHp}
                    />
                </div>

                {/* Lado del Jugador 1 (Izquierda) */}
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', order: 1 }}>
                    <div
                        key={`p1-${player1Index}`}
                        className={platform1Classes}
                    >
                        <img
                            src={player1Pokemon.sprite}
                            alt={player1Pokemon.name}
                            className="pokemon-sprite"
                        />
                        <div className="pokemon-base" />
                    </div>
                </div>

                {/* Proyectil */}
                {animationState.projectile && (
                    <div
                        className={`
                            projectile 
                            ${animationState.projectile.type || ''} 
                            ${animationState.projectile.direction}
                        `}
                    />
                )}

                {/* Lado del Jugador 2 (Derecha) */}
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', order: 3 }}>
                    <div
                        key={`p2-${player2Index}`}
                        className={platform2Classes}
                    >
                        <img
                            src={player2Pokemon.sprite}
                            alt={player2Pokemon.name}
                            className="pokemon-sprite"
                        />
                        <div className="pokemon-base" />
                    </div>
                </div>

                {/* Caja de Diálogo */}
                <div className="dialog-box">
                    <TypewriterText text={dialogMessage} />
                </div>
            </div>

            {/* Panel del Pokémon activo: ataques o selección de cambio */}
            <div className={`attack-panel attack-panel-${currentTurn === 'player1' ? 'left' : 'right'}`}>
                <div className="attack-panel-header">
                    <span className="attack-panel-turn">
                        Turno de {getTrainerName(currentTurn)} · {activeAttacker.name}
                    </span>
                    {!isSwitching ? (
                        <button
                            type="button"
                            className="reset-btn"
                            onClick={handleSwitchClick}
                            disabled={isTurnInProgress || battleOver || !canSwitch}
                            title={canSwitch ? 'Cambia tu Pokémon activo (consume el turno)' : 'No tienes más Pokémon disponibles'}
                        >
                            Cambiar
                        </button>
                    ) : (
                        <button
                            type="button"
                            className="reset-btn"
                            onClick={handleSwitchCancel}
                            disabled={isTurnInProgress}
                        >
                            Volver
                        </button>
                    )}
                </div>

                {!isSwitching ? (
                    <div className="attack-buttons">
                        {activeAttacker.attacks.map((attack) => (
                            <button
                                key={attack.name}
                                type="button"
                                className={`attack-btn attack-type-${attack.type.toLowerCase()}`}
                                onClick={() => executeAttack(attack)}
                                disabled={!showAttackPanel}
                                title={`Daño base: ${attack.damage} · Tipo: ${attack.type}`}
                            >
                                <span className="attack-name">{attack.name}</span>
                                <span className="attack-meta">
                                    {attack.type} · {attack.damage}
                                </span>
                            </button>
                        ))}
                    </div>
                ) : (
                    <div className="switch-options">
                        {switchableOptions.map(({ pokemon, index }) => (
                            <button
                                key={`${pokemon.name}-${index}`}
                                type="button"
                                className={`switch-option attack-type-${pokemon.type.toLowerCase()}`}
                                onClick={() => performSwitch(index)}
                                disabled={isTurnInProgress}
                            >
                                <img
                                    src={pokemon.sprite}
                                    alt={pokemon.name}
                                    className="switch-option-sprite"
                                />
                                <div className="switch-option-info">
                                    <span className="attack-name">{pokemon.name}</span>
                                    <span className="attack-meta">
                                        {pokemon.type} · HP {pokemon.hp}/{pokemon.maxHp}
                                    </span>
                                </div>
                            </button>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
};

export default BattleArena;
