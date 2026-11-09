import React from 'react';
import './VictoryScreen.css';
import { TYPE_LABELS } from '../constants/typeChart';
import Panel from './ui/Panel';
import Button from './ui/Button';

const CONFETTI = [
    { left: '6%', delay: '0s', duration: '3.6s', color: '#ffcb05', size: 10 },
    { left: '14%', delay: '0.8s', duration: '4.2s', color: '#ee1515', size: 8 },
    { left: '22%', delay: '0.2s', duration: '3.4s', color: '#3b4cca', size: 12 },
    { left: '31%', delay: '1.4s', duration: '4.6s', color: '#ffde00', size: 9 },
    { left: '39%', delay: '0.5s', duration: '3.8s', color: '#ff4d4d', size: 11 },
    { left: '48%', delay: '1.1s', duration: '4.1s', color: '#ffcb05', size: 8 },
    { left: '57%', delay: '0.3s', duration: '3.5s', color: '#3b4cca', size: 10 },
    { left: '66%', delay: '1.6s', duration: '4.4s', color: '#ee1515', size: 9 },
    { left: '74%', delay: '0.7s', duration: '3.9s', color: '#ffde00', size: 12 },
    { left: '83%', delay: '1.2s', duration: '4.3s', color: '#3b4cca', size: 8 },
    { left: '91%', delay: '0.4s', duration: '3.7s', color: '#ffcb05', size: 11 },
    { left: '18%', delay: '2s', duration: '4.5s', color: '#ff4d4d', size: 7 },
    { left: '52%', delay: '1.9s', duration: '3.3s', color: '#ee1515', size: 10 },
    { left: '78%', delay: '2.2s', duration: '4.8s', color: '#3b4cca', size: 9 },
];

const CHARACTER_ART = {
    chica: { src: '/media/trainer-girl.webp', label: 'Chica' },
    chico: { src: '/media/trainer-boy.webp', label: 'Chico' },
};

const typeLabel = (type) => TYPE_LABELS[type] || type;

const Trophy = () => (
    <svg className="victory-trophy" viewBox="0 0 128 128" aria-hidden="true">
        <defs>
            <linearGradient id="victory-gold" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#fff6c2" />
                <stop offset="48%" stopColor="#ffd700" />
                <stop offset="100%" stopColor="#e6a800" />
            </linearGradient>
        </defs>
        <circle cx="64" cy="52" r="46" className="victory-trophy-glow" />
        <path
            d="M36 36c-16 2-22 18-14 28 6 8 16 8 20 1"
            className="victory-trophy-handle"
        />
        <path
            d="M92 36c16 2 22 18 14 28-6 8-16 8-20 1"
            className="victory-trophy-handle"
        />
        <path
            d="M40 24h48l-6 40a24 24 0 0 1-36 0z"
            className="victory-trophy-cup"
            fill="url(#victory-gold)"
        />
        <polygon
            points="64,36 68,46 78,46 70,52 73,62 64,56 55,62 58,52 50,46 60,46"
            className="victory-trophy-star"
        />
        <rect x="56" y="78" width="16" height="14" rx="2" className="victory-trophy-cup" fill="url(#victory-gold)" />
        <rect x="38" y="92" width="52" height="10" rx="3" className="victory-trophy-cup" fill="url(#victory-gold)" />
        <rect x="30" y="102" width="68" height="8" rx="3" className="victory-trophy-base" />
    </svg>
);

const VictoryScreen = ({ trainerName, gender, team = [], onRestart }) => {
    const character = CHARACTER_ART[gender] || CHARACTER_ART.chico;

    return (
        <section className="victory-screen" aria-labelledby="victory-title">
            <div className="victory-confetti" aria-hidden="true">
                {CONFETTI.map((piece, index) => (
                    <span
                        key={index}
                        className="victory-confetti-piece"
                        style={{
                            left: piece.left,
                            animationDelay: piece.delay,
                            animationDuration: piece.duration,
                            backgroundColor: piece.color,
                            width: piece.size,
                            height: piece.size * 1.5,
                        }}
                    />
                ))}
            </div>

            <Panel variant="light" padding="lg" className="victory-card">
                <Trophy />
                <p className="victory-kicker">¡Campeón!</p>
                <h1 id="victory-title" className="victory-title pk-title-stroke">
                    ¡Victoria!
                </h1>
                <p className="victory-congrats">
                    ¡Felicidades, <strong>{trainerName}</strong>!
                </p>
                <p className="victory-subtitle">Tu equipo ha ganado el combate.</p>

                <div className="victory-layout">
                    <figure className="victory-character">
                        <img src={character.src} alt={`Personaje de ${trainerName}`} />
                        <figcaption>{character.label}</figcaption>
                    </figure>

                    <div className="victory-team">
                        <h2>Tu equipo</h2>
                        <ul>
                            {team.map((pokemon) => (
                                <li key={pokemon.name}>
                                    {pokemon.sprite ? (
                                        <img src={pokemon.sprite} alt="" />
                                    ) : (
                                        <span className="victory-pokemon-fallback" aria-hidden="true">?</span>
                                    )}
                                    <span className="victory-pokemon-name">{pokemon.name}</span>
                                    <span className="victory-pokemon-types">
                                        {(pokemon.types || []).map(typeLabel).join(' / ')}
                                    </span>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>

                <Button variant="primary" size="lg" onClick={onRestart}>
                    Elegir personaje
                </Button>
            </Panel>
        </section>
    );
};

export default VictoryScreen;
