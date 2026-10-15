import { getTypeMultiplier } from './typeChart';

// Fórmula oficial de daño simplificada (sin aleatoriedad, críticos ni climas).
// Replica com.pokemon.DamageCalculator.java: ambos deben dar exactamente el mismo número.
export const LEVEL = 50;
export const STAB = 1.5;

export const ATTACK_CATEGORIES = {
    FISICO: 'FISICO',
    ESPECIAL: 'ESPECIAL',
};

/**
 * @param attack   { power, type, category }
 * @param attacker { types, attack, specialAttack }
 * @param defender { types, defense, specialDefense }
 * @returns {{ damage: number, effectiveness: number }}
 */
export const calculateDamage = (attack, attacker, defender) => {
    const effectiveness = getTypeMultiplier(attack.type, defender.types);
    if (effectiveness === 0 || attack.power === 0) {
        return { damage: 0, effectiveness };
    }

    const physical = attack.category === ATTACK_CATEGORIES.FISICO;
    const attackStat = physical ? attacker.attack : attacker.specialAttack;
    const defenseStat = physical ? defender.defense : defender.specialDefense;

    const levelFactor = Math.floor((2 * LEVEL) / 5) + 2;
    const base = Math.floor(Math.floor((levelFactor * attack.power * attackStat) / defenseStat) / 50) + 2;
    const stab = attacker.types.includes(attack.type) ? STAB : 1.0;

    const damage = Math.floor(base * stab * effectiveness);
    return { damage: Math.max(1, damage), effectiveness };
};
