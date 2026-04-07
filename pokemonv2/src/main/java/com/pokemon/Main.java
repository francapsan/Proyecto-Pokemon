package com.pokemon;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("--- BIENVENIDO AL CAMPEONATO POKÉMON ---");

        Trainer player1 = createTrainer(1);
        Trainer player2 = createTrainer(2);

        System.out.println("\n--- FASE DE SELECCIÓN DE POKÉMON ---");
        startDraft(player1, player2);

        System.out.println("\n¡QUE COMIENCE EL DUELO!");
        runBattle(player1, player2);
    }

    private static Trainer createTrainer(int playerNumber) {
        System.out.print("\nJugador " + playerNumber + ", introduce tu nombre: ");
        String name = scanner.nextLine();
        System.out.print("¿Eres chico o chica? (Aunque ahora no influya): ");
        String gender = scanner.nextLine();
        return new Trainer(name, gender);
    }

    private static void startDraft(Trainer p1, Trainer p2) {
        for (int i = 1; i <= 3; i++) {
            System.out.println("\nTurno de selección nº " + i);
            p1.addToTeam(pickPokemon(p1.getName()));
            p2.addToTeam(pickPokemon(p2.getName()));
        }
    }

    private static Pokemon pickPokemon(String trainerName) {
        System.out.println(trainerName + ", elige a un compañero:");
        System.out.println("1. Blastoise (Agua)\n2. Charizard (Fuego)\n3. Venusaur (Planta)");
        
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                Pokemon blastoise = new Pokemon("Blastoise", "AGUA", 100, 78);
                blastoise.learnAttack(new Attack("Hidrobomba", 40, "AGUA"));
                blastoise.learnAttack(new Attack("Mordisco", 20, "NORMAL"));
                blastoise.learnAttack(new Attack("Puño Certero", 25, "NORMAL"));
                return blastoise;
            case 2:
                Pokemon charizard = new Pokemon("Charizard", "FUEGO", 100, 100);
                charizard.learnAttack(new Attack("Lanzallamas", 40, "FUEGO"));
                charizard.learnAttack(new Attack("Cola Dragon", 20, "NORMAL"));
                charizard.learnAttack(new Attack("Vuelo", 25, "NORMAL"));
                return charizard;
            case 3:
                Pokemon venusaur = new Pokemon("Venusaur", "PLANTA", 100, 80);
                venusaur.learnAttack(new Attack("Rayo Solar", 40, "PLANTA"));
                venusaur.learnAttack(new Attack("Hoja Afilada", 20, "NORMAL"));
                venusaur.learnAttack(new Attack("Terremoto", 25, "NORMAL"));
                return venusaur;
            default:
                System.out.println("Opción no válida. Por defecto eliges a Blastoise.");
                return new Pokemon("Blastoise", "AGUA", 100, 78);
        }
    }

    private static void runBattle(Trainer p1, Trainer p2) {
        Pokemon activeP1 = p1.getTeam().get(0);
        Pokemon activeP2 = p2.getTeam().get(0);

        while (activeP1.getHp() > 0 && activeP2.getHp() > 0) {
            System.out.println("\n" + activeP1.getName() + " (" + activeP1.getHp() + " HP) VS " 
                               + activeP2.getName() + " (" + activeP2.getHp() + " HP)");
            if (activeP1.getSpeed() >= activeP2.getSpeed()) {
                executeTurn(activeP1, activeP2);
                if (activeP2.getHp() > 0) executeTurn(activeP2, activeP1);
            } else {
                executeTurn(activeP2, activeP1);
                if (activeP1.getHp() > 0) executeTurn(activeP1, activeP2);
            }
        }

        System.out.println("\n--- FIN DEL COMBATE ---");
        String winner = (activeP1.getHp() > 0) ? activeP1.getName() : activeP2.getName();
        System.out.println("¡El ganador es " + winner + "!");
    }

private static void executeTurn(Pokemon attacker, Pokemon defender) {
    System.out.println("\nTurno del " + attacker.getName() + " de " + attacker.getOwnerName() + ". Elige ataque (1-3):");
    for (int i = 0; i < 3; i++) {
        System.out.println((i + 1) + ". " + attacker.getAttacks().get(i).getName());
    }

    int moveChoice = scanner.nextInt();
    Attack selectedAttack = attacker.getAttacks().get(moveChoice - 1);
    
    System.out.println("¡El " + attacker.getName() + " de " + attacker.getOwnerName() + " usa " + selectedAttack.getName() + "!");
    defender.receiveDamage(selectedAttack, attacker);
}
}