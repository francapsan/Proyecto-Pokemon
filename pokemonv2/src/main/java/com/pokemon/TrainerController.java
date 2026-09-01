package com.pokemon;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    @PostMapping("/create")
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody TrainerDTO trainerDTO) {
        Trainer newTrainer = new Trainer(trainerDTO.name(), trainerDTO.gender());
        return ResponseEntity.ok(TrainerResponse.fromTrainer(newTrainer));
    }
}
