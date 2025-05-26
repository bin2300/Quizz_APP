package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class QuestionLoader {
    private static final String RESOURCE_DATA_FOLDER = "data";
    private static final Path EXTERNAL_DATA_FOLDER = Paths.get("../generated_data");
    private static final int DEFAULT_TIME_LIMIT = 30;

    /**
     * Charge tous les quizzes depuis les deux dossiers :
     * - /resources/data (classpath)
     * - /generated_data (répertoire externe persistant)
     */
    public static Map<String, List<Question>> loadAllQuizzes() {
        Map<String, List<Question>> quizzes = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        // Charger depuis resources/data
        try {
            URL folderUrl = QuestionLoader.class.getClassLoader().getResource(RESOURCE_DATA_FOLDER);

            if (folderUrl != null) {
                Path folderPath = Paths.get(folderUrl.toURI());
                loadFromDirectory(folderPath, quizzes, mapper);
            } else {
                System.err.println("⚠️ Dossier 'resources/data' introuvable.");
            }
        } catch (URISyntaxException | IOException e) {
            System.err.println("Erreur lors de l'accès à 'resources/data' :");
            e.printStackTrace();
        }
                // System.out.println("Chargement de generated_data [Alerte 1]");
                
        // Charger depuis generated_data/
        if (Files.exists(EXTERNAL_DATA_FOLDER)) {
            try {
                // System.out.println("Chargement de generated_data");
                loadFromDirectory(EXTERNAL_DATA_FOLDER, quizzes, mapper);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement depuis 'generated_data' :");
                e.printStackTrace();
            }
        }
        return quizzes;
    }

    private static void loadFromDirectory(Path folderPath, Map<String, List<Question>> quizzes, ObjectMapper mapper)
            throws IOException {
        DirectoryStream<Path> jsonFiles = Files.newDirectoryStream(folderPath, "*.json");

        for (Path filePath : jsonFiles) {
            String filename = filePath.getFileName().toString();

            try {
                List<Question> questions = mapper.readValue(filePath.toFile(), new TypeReference<List<Question>>() {});

                for (Question q : questions) {
                    if (q.getTimeLimitSeconds() <= 0) {
                        q.setTimeLimitSeconds(DEFAULT_TIME_LIMIT);
                    }
                }

                if (isValidQuizz(questions)) {
                    String quizName = filename.replace(".json", "");
                    quizzes.putIfAbsent(quizName, questions); // évite d’écraser un quiz déjà chargé
                } else {
                    System.err.println("❌ Fichier invalide : " + filename + " (ignoré)");
                }

            } catch (IOException e) {
                System.err.println("❌ Erreur de lecture : " + filename + " (ignoré)");
            }
        }
    }

    private static boolean isValidQuizz(List<Question> questions) {
        return questions != null && !questions.isEmpty() && questions.stream().allMatch(q ->
            q.getQuestionText() != null &&
            q.getOptions() != null &&
            q.getOptions().size() >= 2 &&
            q.getCorrectIndex() >= 0 &&
            q.getCorrectIndex() < q.getOptions().size() &&
            q.getTimeLimitSeconds() > 0
        );
    }
}
