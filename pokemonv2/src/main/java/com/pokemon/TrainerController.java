package com.pokemon;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class TrainerController {

    private static final String EMPTY_NAME_ERROR = "El nombre no puede estar vacío.";
    private static final String INVALID_GENDER_ERROR = "Opción no válida. Por favor, responde 'chico' o 'chica'.";
    private static final String VALID_MALE = "chico";
    private static final String VALID_FEMALE = "chica";

    @PostMapping("/create")
    public ResponseEntity<?> createTrainer(@RequestBody TrainerDTO trainerDTO) {
        try {
            validateTrainerDTO(trainerDTO);
            Trainer newTrainer = new Trainer(trainerDTO.name().trim(), trainerDTO.gender().toLowerCase());
            return ResponseEntity.ok(newTrainer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void validateTrainerDTO(TrainerDTO trainerDTO) {
        if (trainerDTO == null) {
            throw new IllegalArgumentException("Los datos del entrenador no pueden ser nulos.");
        }
        if (trainerDTO.name() == null || trainerDTO.name().trim().isEmpty()) {
            throw new IllegalArgumentException(EMPTY_NAME_ERROR);
        }
        if (trainerDTO.gender() == null || 
            (!trainerDTO.gender().equalsIgnoreCase(VALID_MALE) && 
             !trainerDTO.gender().equalsIgnoreCase(VALID_FEMALE))) {
            throw new IllegalArgumentException(INVALID_GENDER_ERROR);
        }
    }
}