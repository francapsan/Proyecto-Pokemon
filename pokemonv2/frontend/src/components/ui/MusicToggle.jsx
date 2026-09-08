import React from 'react';
import './MusicToggle.css';

/**
 * Botón de control de música compartido en las 3 pantallas.
 * Solo se encarga de la parte visual: la lógica de audio queda en cada pantalla.
 *
 * @param {boolean} isPlaying   ¿La música está sonando?
 * @param {Function} onToggle   Callback que se ejecuta al pulsar el botón.
 * @param {'top-right' | 'top-center'} [position='top-right']
 *   Ubicación en pantalla. 'top-center' se usa en el combate para no
 *   colisionar con las cajas de HP de la esquina.
 * @param {string}   [className] Clases extra opcionales.
 */
const MusicToggle = ({
    isPlaying,
    onToggle,
    position = 'top-right',
    className = '',
}) => {
    return (
        <button
            type="button"
            className={`pk-music-toggle pk-music-toggle--${position} ${className}`.trim()}
            onClick={onToggle}
            aria-label={isPlaying ? 'Silenciar música' : 'Activar música'}
            aria-pressed={isPlaying}
        >
            {isPlaying ? '🔊' : '🔇'}
        </button>
    );
};

export default MusicToggle;
