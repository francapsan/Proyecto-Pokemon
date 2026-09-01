package com.pokemon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TrainerDTO(
        @NotBlank(message = "El nombre no puede estar vacío.")
        String name,

        @NotBlank(message = "El género no puede estar vacío.")
        @Pattern(regexp = "(?i)chico|chica",
                 message = "Opción no válida. Por favor, responde 'chico' o 'chica'.")
        String gender
) {
}
