package com.pokemon;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

/**
 * Test slice del controller REST.
 * Nota: en Spring Boot 4.x @WebMvcTest vive en el módulo
 * `spring-boot-starter-webmvc-test` (paquete org.springframework.boot.webmvc.test.autoconfigure).
 */
@WebMvcTest(TrainerController.class)
@Import(GlobalExceptionHandler.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createsTrainerSuccessfully() throws Exception {
        String body = mapper.writeValueAsString(new TrainerDTO("Ash", "chico"));

        mockMvc.perform(post("/api/trainers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ash"))
                .andExpect(jsonPath("$.gender").value("chico"));
    }

    @Test
    void rejectsBlankName() throws Exception {
        String body = mapper.writeValueAsString(new TrainerDTO("  ", "chica"));

        mockMvc.perform(post("/api/trainers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("nombre")));
    }

    @Test
    void rejectsInvalidGender() throws Exception {
        String body = mapper.writeValueAsString(new TrainerDTO("Misty", "otro"));

        mockMvc.perform(post("/api/trainers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("chico")));
    }
}
