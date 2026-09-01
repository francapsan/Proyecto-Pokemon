package com.pokemon;

import java.util.List;

/**
 * Vista pública del entrenador expuesta por la API.
 * Evita filtrar detalles internos de la entidad {@link Trainer}.
 */
public record TrainerResponse(String name, String gender, List<String> team) {

    public static TrainerResponse fromTrainer(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo");
        }
        List<String> teamNames = trainer.getTeam().stream()
                .map(pokemon -> pokemon.getName())
                .toList();
        return new TrainerResponse(trainer.getName(), trainer.getGender(), teamNames);
    }
}
