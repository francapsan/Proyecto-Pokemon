import React from 'react';
import MusicToggle from './MusicToggle';
import './PageShell.css';

/**
 * Marco compartido por las 3 pantallas del juego.
 * Aporta la identidad visual constante (logo + migas de progreso + botón
 * de música siempre en la misma posición) que ancla la experiencia y evita
 * que cada fase parezca una app distinta.
 *
 * @param {'trainer' | 'selection' | 'battle'} currentStep  Fase activa.
 * @param {'full' | 'compact' | 'hidden'} [chrome='full']
 *   - 'full':    logo grande + migas destacadas (setup, selección).
 *   - 'compact': solo migas pequeñas con opacidad reducida (combate).
 *   - 'hidden':  nada (útil durante modales, vídeos a pantalla completa).
 * @param {boolean} [isMusicPlaying]     Estado del audio.
 * @param {Function} [onToggleMusic]     Callback para silenciar/activar.
 * @param {'top-right' | 'top-center'} [musicPosition='top-right']
 *   Ubicación del MusicToggle. 'top-center' evita colisión con los
 *   letreros de HP de las esquinas en la pantalla de combate.
 * @param {React.ReactNode} children     Contenido específico de la fase.
 */
const STEPS = [
    { id: 'trainer',   label: 'Entrenador' },
    { id: 'selection', label: 'Equipo' },
    { id: 'battle',    label: 'Combate' },
];

const PageShell = ({
    currentStep,
    chrome = 'full',
    isMusicPlaying,
    onToggleMusic,
    musicPosition = 'top-right',
    children,
}) => {
    const currentIndex = STEPS.findIndex(s => s.id === currentStep);

    const renderBreadcrumbs = () => (
        <nav
            className={`pk-shell__breadcrumbs pk-shell__breadcrumbs--${chrome}`}
            aria-label="Progreso"
        >
            {STEPS.map((step, index) => {
                const isActive = index === currentIndex;
                const isDone = index < currentIndex;
                const state = isActive ? 'active' : isDone ? 'done' : 'pending';
                return (
                    <React.Fragment key={step.id}>
                        <span
                            className={`pk-crumb pk-crumb--${state}`}
                            aria-current={isActive ? 'step' : undefined}
                        >
                            {isDone && <span className="pk-crumb__tick" aria-hidden>✓</span>}
                            {step.label}
                        </span>
                        {index < STEPS.length - 1 && (
                            <span className="pk-crumb__sep" aria-hidden>→</span>
                        )}
                    </React.Fragment>
                );
            })}
        </nav>
    );

    return (
        <div className={`pk-shell pk-shell--chrome-${chrome}`}>
            {chrome === 'full' && (
                <header className="pk-shell__header">
                    <div className="pk-shell__logo pk-title-stroke" aria-label="Pokémon">
                        POKÉMON
                    </div>
                    {renderBreadcrumbs()}
                </header>
            )}

            {chrome === 'compact' && (
                <div className="pk-shell__compact-nav">
                    {renderBreadcrumbs()}
                </div>
            )}

            {typeof onToggleMusic === 'function' && (
                <MusicToggle
                    isPlaying={!!isMusicPlaying}
                    onToggle={onToggleMusic}
                    position={musicPosition}
                />
            )}

            <main className="pk-shell__main">{children}</main>
        </div>
    );
};

export default PageShell;
