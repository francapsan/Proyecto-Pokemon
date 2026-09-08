import React from 'react';
import './Button.css';

/**
 * Botón unificado con las variantes neobrutalistas del proyecto.
 * Reemplaza a start-btn, start-button, gender-btn, action-btn, attack-btn...
 *
 * @param {'primary' | 'secondary' | 'ghost' | 'type-fire' | 'type-water' | 'type-plant' | 'type-normal'} [variant='primary']
 * @param {'sm' | 'md' | 'lg'} [size='md']
 * @param {boolean} [block]        Si true, ocupa el 100% del ancho disponible.
 * @param {boolean} [selected]     Estado "activo/seleccionado" (para toggles).
 * @param {string}  [className]    Clases extra.
 * @param {React.ReactNode} children
 */
const Button = ({
    variant = 'primary',
    size = 'md',
    block = false,
    selected = false,
    className = '',
    type = 'button',
    children,
    ...rest
}) => {
    const classes = [
        'pk-btn',
        `pk-btn--${variant}`,
        `pk-btn--${size}`,
        block && 'pk-btn--block',
        selected && 'pk-btn--selected',
        className,
    ]
        .filter(Boolean)
        .join(' ');

    return (
        <button type={type} className={classes} {...rest}>
            {children}
        </button>
    );
};

export default Button;
