package com.pokemon;

/**
 * Fórmula oficial de daño simplificada (sin aleatoriedad, críticos ni climas).
 * Debe coincidir exactamente con {@code frontend/src/constants/damage.js}, que es quien calcula el combate en el cliente.
 */
public final class DamageCalculator {

    public static final double STAB = 1.5;

    public record Result(int damage, double effectiveness) {}

    private DamageCalculator() {}

    public static Result calculate(Attack attack, Pokemon attacker, Pokemon defender) {
        double effectiveness = TypeChart.getMultiplier(attack.getType(), defender.getTypes());
        if (effectiveness == 0.0 || attack.getPower() == 0) {
            return new Result(0, effectiveness);
        }

        boolean physical = attack.getCategory() == AttackCategory.FISICO;
        int attackStat = physical ? attacker.getStats().attack() : attacker.getStats().specialAttack();
        int defenseStat = physical ? defender.getStats().defense() : defender.getStats().specialDefense();

        int levelFactor = 2 * Stats.LEVEL / 5 + 2;
        int base = levelFactor * attack.getPower() * attackStat / defenseStat / 50 + 2;
        double stab = attacker.getTypes().contains(attack.getType()) ? STAB : 1.0;

        int damage = (int) Math.floor(base * stab * effectiveness);
        return new Result(Math.max(1, damage), effectiveness);
    }
}
