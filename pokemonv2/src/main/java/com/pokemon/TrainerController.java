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

    @PostMapping("/create")
    public ResponseEntity<?> createTrainer(@RequestBody TrainerDTO trainerDTO) {
        if (trainerDTO.name() == null || trainerDTO.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío.");
        }

        String gender = trainerDTO.gender();
        if (gender == null || (!gender.equalsIgnoreCase("chico") && !gender.equalsIgnoreCase("chica"))) {
            return ResponseEntity.badRequest().body("Opción no válida. Por favor, responde 'chico' o 'chica'.");
        }

        Trainer newTrainer = new Trainer(trainerDTO.name().trim(), gender.toLowerCase());
        return ResponseEntity.ok(newTrainer);
    }
}