package com.algoj;

import com.algoj.dto.AlgorithmRunRequest;
import com.algoj.dto.DataStructureOpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/health returns UP status and Java runtime info")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("AlgoJ Java Spring Boot Backend"));
    }

    @Test
    @DisplayName("GET /api/catalog returns comprehensive DSA catalog")
    void testCatalogEndpoint() throws Exception {
        mockMvc.perform(get("/api/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sorting").isArray())
                .andExpect(jsonPath("$.graphs").isArray());
    }

    @Test
    @DisplayName("POST /api/algorithms/run executes Bubble Sort and returns ordered steps")
    void testRunBubbleSort() throws Exception {
        AlgorithmRunRequest req = new AlgorithmRunRequest();
        req.setAlgorithmId("bubble-sort");
        req.setArray(List.of(5, 2, 8, 1, 9));

        mockMvc.perform(post("/api/algorithms/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithmId").value("bubble-sort"))
                .andExpect(jsonPath("$.totalSteps").isNumber())
                .andExpect(jsonPath("$.steps[0].stepIndex").value(0))
                .andExpect(jsonPath("$.steps[0].operationType").value("init"));
    }

    @Test
    @DisplayName("POST /api/structures/operate executes Stack push and returns trace")
    void testOperateStack() throws Exception {
        DataStructureOpRequest req = new DataStructureOpRequest();
        req.setStructureId("stacks");
        req.setOperation("push");
        req.setValue(88);

        mockMvc.perform(post("/api/structures/operate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithmId").value("stacks"))
                .andExpect(jsonPath("$.steps").isArray());
    }

    @Test
    @DisplayName("POST /api/algorithms/run with invalid algorithmId returns 400 Bad Request with JSON error")
    void testInvalidAlgorithmError() throws Exception {
        AlgorithmRunRequest req = new AlgorithmRunRequest();
        req.setAlgorithmId("unknown-algorithm-xyz");

        mockMvc.perform(post("/api/algorithms/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
