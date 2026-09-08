import React from 'react';
import './Panel.css';

/**
 * Contenedor de contenido con el estilo neobrutalista del proyecto.
 * Unifica las cards de setup, selección y HUD del combate.
 *
 * @param {'light' | 'dark' | 'hud'} [variant='light']
 *   - 'light': fondo blanco + borde azul + sombra dura (setup, selección).
 *   - 'dark':  fondo negro semitransparente + borde azul (modales).
 *   - 'hud':   fondo negro semitransparente + borde dorado (combate).
 * @param {'sm' | 'md' | 'lg'} [padding='md']
 * @param {string}  [as='div']       Tag HTML a renderizar.
 * @param {string}  [className]      Clases extra.
 */
const Panel = ({
    variant = 'light',
    padding = 'md',
    as: Tag = 'div',
    className = '',
    children,
    ...rest
}) => {
    const classes = [
        'pk-panel',
        `pk-panel--${variant}`,
        `pk-panel--pad-${padding}`,
        className,
    ]
        .filter(Boolean)
        .join(' ');

    return (
        <Tag className={classes} {...rest}>
            {children}
        </Tag>
    );
};

export default Panel;
