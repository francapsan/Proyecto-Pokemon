// Reparte la lista sorteada en un grupo de opciones por jugador, sin repetir entre ellos.
export const splitChoices = (list, perPlayer) => ({
    1: list.slice(0, perPlayer),
    2: list.slice(perPlayer, perPlayer * 2),
});
