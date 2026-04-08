package com.pokemon;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("--- BIENVENIDO AL CAMPEONATO POKÉMON ---");

        Trainer player1 = createTrainer(1);
        Trainer player2 = createTrainer(2);

        System.out.println("\n--- FASE DE SELECCIÓN DE POKÉMON ---");
        startDraft(player1, player2);

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
        System.out.println("\n¡QUE COMIENCE EL DUELO!");

        Pokemon activeP1 = chooseNextPokemon(p1);
        Pokemon activeP2 = chooseNextPokemon(p2);

        while (p1.hasAvailablePokemon() && p2.hasAvailablePokemon()) {
            System.out.println("\n" + "--- NUEVO TURNO ---");
            System.out.println(p1.getName() + ": " + activeP1.getName() + " (" + activeP1.getHp() + " HP)");
            System.out.println(p2.getName() + ": " + activeP2.getName() + " (" + activeP2.getHp() + " HP)");

            Pokemon first, second;
            Trainer firstTrainer, secondTrainer;

            if (activeP1.getSpeed() >= activeP2.getSpeed()) {
                first = activeP1;
                firstTrainer = p1;
                second = activeP2;
                secondTrainer = p2;
            } else {
                first = activeP2;
                firstTrainer = p2;
                second = activeP1;
                secondTrainer = p1;
            }

            // Turno del primer atacante
            executeTurn(first, second);

            // Comprobar si el defensor fue derrotado
            if (second.getHp() <= 0) {
                System.out.println("\n¡" + second.getName() + " de " + second.getOwnerName() + " ha sido derrotado!");
                if (secondTrainer.hasAvailablePokemon()) {
                    Pokemon newPokemon = chooseNextPokemon(secondTrainer);
                    if (secondTrainer == p1) {
                        activeP1 = newPokemon;
                    } else {
                        activeP2 = newPokemon;
                    }
                    continue; // Reinicia el bucle para el nuevo enfrentamiento
                } else {
                    break; // El entrenador no tiene más Pokémon, termina la batalla
                }
            }

            // Turno del segundo atacante
            executeTurn(second, first);

            // Comprobar si el primer atacante fue derrotado
            if (first.getHp() <= 0) {
                System.out.println("\n¡" + first.getName() + " de " + first.getOwnerName() + " ha sido derrotado!");
                if (firstTrainer.hasAvailablePokemon()) {
                    Pokemon newPokemon = chooseNextPokemon(firstTrainer);
                    if (firstTrainer == p1) {
                        activeP1 = newPokemon;
                    } else {
                        activeP2 = newPokemon;
                    }
                    continue; // Reinicia el bucle para el nuevo enfrentamiento
                } else {
                    break; // El entrenador no tiene más Pokémon, termina la batalla
                }
            }
        }

        System.out.println("\n--- FIN DEL COMBATE ---");
        if (!p1.hasAvailablePokemon()) {
            System.out.println("¡" + p1.getName() + " no tiene más Pokémon disponibles!");
            System.out.println("¡El ganador del campeonato es " + p2.getName() + "!");
        } else {
            System.out.println("¡" + p2.getName() + " no tiene más Pokémon disponibles!");
            System.out.println("¡El ganador del campeonato es " + p1.getName() + "!");
        }
    }

    private static Pokemon chooseNextPokemon(Trainer trainer) {
        System.out.println("\n" + trainer.getName() + ", elige tu Pokémon:");
        
        List<Pokemon> availablePokemon = new ArrayList<>();
        for (Pokemon p : trainer.getTeam()) {
            if (p.getHp() > 0) {
                availablePokemon.add(p);
            }
        }

        if (availablePokemon.size() == 1) {
            Pokemon onlyChoice = availablePokemon.get(0);
            System.out.println("¡Solo te queda " + onlyChoice.getName() + "! ¡Adelante, " + onlyChoice.getName() + "!");
            return onlyChoice;
        }

        for (int i = 0; i < availablePokemon.size(); i++) {
            Pokemon p = availablePokemon.get(i);
            System.out.println((i + 1) + ". " + p.getName() + " (" + p.getHp() + " HP)");
        }

        int choice = 0;
        Pokemon chosenPokemon = null;
        while (chosenPokemon == null) {
            System.out.print("Elige un Pokémon (1-" + availablePokemon.size() + "): ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (choice >= 1 && choice <= availablePokemon.size()) {
                    chosenPokemon = availablePokemon.get(choice - 1);
                } else {
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
        
        System.out.println("¡Adelante, " + chosenPokemon.getName() + "!");
        return chosenPokemon;
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