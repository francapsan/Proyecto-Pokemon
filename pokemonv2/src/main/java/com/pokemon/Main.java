package com.pokemon;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static final Map<Integer, Supplier<Pokemon>> POKEMON_CREATORS = new LinkedHashMap<>();

    public static void main(String[] args) {
        System.out.println("--- BIENVENIDO AL CAMPEONATO POKÉMON ---");

        Trainer player1 = createTrainer(1);
        Trainer player2 = createTrainer(2);

        System.out.println("\n--- FASE DE SELECCIÓN DE POKÉMON ---");
        startDraft(player1, player2);

        runBattle(player1, player2);
    }

    static {
        POKEMON_CREATORS.put(1, () -> {
            Pokemon p = new Pokemon("Blastoise", PokemonType.AGUA, 100, 78);
            p.learnAttack(new Attack("Hidrobomba", 40, PokemonType.AGUA));
            p.learnAttack(new Attack("Mordisco", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Puño Certero", 25, PokemonType.NORMAL));
            return p;
        });
        POKEMON_CREATORS.put(2, () -> {
            Pokemon p = new Pokemon("Charizard", PokemonType.FUEGO, 100, 100);
            p.learnAttack(new Attack("Lanzallamas", 40, PokemonType.FUEGO));
            p.learnAttack(new Attack("Cola Dragon", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Vuelo", 25, PokemonType.NORMAL));
            return p;
        });
        POKEMON_CREATORS.put(3, () -> {
            Pokemon p = new Pokemon("Venusaur", PokemonType.PLANTA, 100, 80);
            p.learnAttack(new Attack("Rayo Solar", 40, PokemonType.PLANTA));
            p.learnAttack(new Attack("Hoja Afilada", 20, PokemonType.NORMAL));
            p.learnAttack(new Attack("Terremoto", 25, PokemonType.NORMAL));
            return p;
        });
    }

    private static Trainer createTrainer(int playerNumber) {
        System.out.print("\nJugador " + playerNumber + ", introduce tu nombre: ");
        String name = scanner.nextLine();
        String gender;
        while (true) {
            System.out.print("¿Eres chico o chica?: ");
            gender = scanner.nextLine().trim().toLowerCase();
            if (gender.equals("chico") || gender.equals("chica")) {
                break;
            } else {
                System.out.println("Opción no válida. Por favor, responde 'chico' o 'chica'.");
            }
        }
        return new Trainer(name, gender);
    }

    private static void startDraft(Trainer p1, Trainer p2) {
        List<Integer> availablePokemonKeys = new ArrayList<>(POKEMON_CREATORS.keySet());
        for (int i = 1; i <= Trainer.MAX_TEAM_SIZE; i++) {
            System.out.println("\nTurno de selección nº " + i);
            if (availablePokemonKeys.isEmpty()) break;
            p1.addToTeam(pickPokemon(p1.getName(), availablePokemonKeys));
            
            if (availablePokemonKeys.isEmpty()) break;
            p2.addToTeam(pickPokemon(p2.getName(), availablePokemonKeys));
        }
    }

    private static Pokemon pickPokemon(String trainerName, List<Integer> availableKeys) {
        System.out.println(trainerName + ", elige a un compañero:");
        availableKeys.forEach(key -> {
            // Llamamos a get() solo para mostrar el nombre y el tipo. Es un poco ineficiente
            // pero aceptable para la escala de esta aplicación.
            Pokemon p = POKEMON_CREATORS.get(key).get();
            System.out.printf("%d. %s (%s)\n", key, p.getName(), p.getType().name());
        });

        while (true) {
            System.out.print("Elige un Pokémon de la lista: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (availableKeys.contains(choice)) {
                    availableKeys.remove(Integer.valueOf(choice)); // Eliminar por objeto, no por índice
                    return POKEMON_CREATORS.get(choice).get();
                } else {
                    System.out.println("Opción no válida o Pokémon ya elegido. Por favor, elige de la lista.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                scanner.nextLine(); // Consume the invalid input
            }
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
                if (!secondTrainer.hasAvailablePokemon()) break;
                
                Pokemon newPokemon = chooseNextPokemon(secondTrainer);
                if (secondTrainer == p1) activeP1 = newPokemon; else activeP2 = newPokemon;
                continue; // Reinicia el bucle para el nuevo enfrentamiento
            }

            // Turno del segundo atacante
            executeTurn(second, first);

            // Comprobar si el primer atacante fue derrotado
            if (first.getHp() <= 0) {
                System.out.println("\n¡" + first.getName() + " de " + first.getOwnerName() + " ha sido derrotado!");
                if (!firstTrainer.hasAvailablePokemon()) break;

                Pokemon newPokemon = chooseNextPokemon(firstTrainer);
                if (firstTrainer == p1) activeP1 = newPokemon; else activeP2 = newPokemon;
                // No es necesario un 'continue' aquí, el bucle se reiniciará de forma natural
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
        
        List<Pokemon> availablePokemon = trainer.getTeam().stream()
                                                .filter(p -> p.getHp() > 0)
                                                .collect(Collectors.toList());

        if (availablePokemon.size() == 1) {
            Pokemon onlyChoice = availablePokemon.get(0);
            System.out.println("¡Solo te queda " + onlyChoice.getName() + "! ¡Adelante, " + onlyChoice.getName() + "!");
            return onlyChoice;
        }

        for (int i = 0; i < availablePokemon.size(); i++) {
            Pokemon p = availablePokemon.get(i);
            System.out.println((i + 1) + ". " + p.getName() + " (" + p.getHp() + " HP)");
        }

        int choice = getPlayerChoice("Elige un Pokémon", availablePokemon.size());
        Pokemon chosenPokemon = availablePokemon.get(choice - 1);

        System.out.println("¡Adelante, " + chosenPokemon.getName() + "!");
        return chosenPokemon;
    }

    private static void executeTurn(Pokemon attacker, Pokemon defender) {
        System.out.println("\nTurno del " + attacker.getName() + " de " + attacker.getOwnerName() + ". Elige un ataque:");
        for (int i = 0; i < attacker.getAttacks().size(); i++) {
            System.out.println((i + 1) + ". " + attacker.getAttacks().get(i).getName());
        }

        int moveChoice = getPlayerChoice("Elige un ataque", attacker.getAttacks().size());
        Attack selectedAttack = attacker.getAttacks().get(moveChoice - 1);

        System.out.println("¡El " + attacker.getName() + " de " + attacker.getOwnerName() + " usa " + selectedAttack.getName() + "!");
        defender.receiveDamage(selectedAttack, attacker);
    }

    private static int getPlayerChoice(String prompt, int maxOption) {
        while (true) {
            System.out.print(prompt + " (1-" + maxOption + "): ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice >= 1 && choice <= maxOption) {
                    return choice;
                } else {
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }
}