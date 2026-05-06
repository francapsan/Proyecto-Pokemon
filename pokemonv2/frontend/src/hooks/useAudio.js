import { useState, useRef, useCallback } from 'react';
import { AUDIO_CONFIG } from '../constants/config';

/**
 * Custom hook para manejar reproducción de audio
 * @param {string} src - Ruta del archivo de audio
 * @param {number} volume - Volumen (0-1)
 * @param {boolean} loop - Si debe reproducirse en bucle
 * @returns {Object} - { play, pause, toggle, isPlaying, audio }
 */
export const useAudio = (src, volume = 1, loop = false) => {
    const audioRef = useRef(null);
    const [isPlaying, setIsPlaying] = useState(false);

    // Inicializar el audio si no existe
    if (!audioRef.current) {
        audioRef.current = new Audio(src);
        audioRef.current.volume = volume;
        audioRef.current.loop = loop;
    }

    const play = useCallback(() => {
        if (!audioRef.current) return;
        
        audioRef.current.currentTime = 0;
        audioRef.current.play()
            .catch(e => console.error("Error al reproducir audio:", e));
        setIsPlaying(true);
    }, []);

    const pause = useCallback(() => {
        if (!audioRef.current) return;
        
        audioRef.current.pause();
        setIsPlaying(false);
    }, []);

    const toggle = useCallback(() => {
        if (isPlaying) {
            pause();
        } else {
            audioRef.current.play()
                .then(() => setIsPlaying(true))
                .catch(e => {
                    console.error("Error al reproducir audio:", e);
                    setIsPlaying(false);
                });
        }
    }, [isPlaying, pause]);

    return { play, pause, toggle, isPlaying, audio: audioRef.current };
};
