package com.pokemon;

/**
 * Stats finales de un Pokémon en combate.
 * Todos los Pokémon del juego luchan a {@link #LEVEL}, con IV perfectos y sin EV.
 */
public record Stats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {

    public static final int LEVEL = 50;
    private static final int IV = 31;

    public Stats {
        if (hp < 1 || attack < 1 || defense < 1 || specialAttack < 1 || specialDefense < 1 || speed < 1) {
            throw new IllegalArgumentException("Todas las stats deben ser mayores o iguales a 1");
        }
    }

    /** Fórmula oficial de stats (generación 3 en adelante) a nivel 50, sin EV y con naturaleza neutra. */
    public static Stats fromBaseStats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
        return new Stats(
                (2 * hp + IV) * LEVEL / 100 + LEVEL + 10,
                otherStat(attack),
                otherStat(defense),
                otherStat(specialAttack),
                otherStat(specialDefense),
                otherStat(speed));
    }

    private static int otherStat(int base) {
        return (2 * base + IV) * LEVEL / 100 + 5;
    }
}
