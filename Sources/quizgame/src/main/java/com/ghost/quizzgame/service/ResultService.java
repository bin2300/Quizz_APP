package com.ghost.quizzgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ResultService {
    private static final Path RESULT_FILE =  Paths.get("user_data/result.json");

    public static List<Map<String, Object>> getAllResults() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (!Files.exists(RESULT_FILE)) {
                return new ArrayList<>();
            }

            String content = Files.readString(RESULT_FILE).trim();
            if (content.isEmpty()) {
                return new ArrayList<>();
            }

            return mapper.readValue(content, List.class);
        } catch (IOException e) {
            System.err.println("Erreur de lecture des resultats : " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
