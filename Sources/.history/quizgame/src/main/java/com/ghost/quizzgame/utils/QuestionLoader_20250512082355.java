package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class QuestionLoader {
    private static final String QUIZZ_FOLDER = "data";
    private static final int DEFAULT_TIME_LIMIT = 30; // ⏱️ Valeur par défaut si absente

    /**
     * Charge tous les fichiers .json du dossier /resources/data,
     * considère chaque fichier comme un quiz si les questions sont valides.
     */
    public static Map<String, List<Question>> loadAllQuizzes() {
        Map<String, List<Question>> quizzes = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            URL folderUrl = QuestionLoader.class.getClassLoader().getResource(QUIZZ_FOLDER);

            if (folderUrl == null) {
                System.err.println("Dossier 'data' introuvable dans resources.");
                return quizzes;
            }

            Path folderPath = Paths.get(folderUrl.toURI());
            DirectoryStream<Path> jsonFiles = Files.newDirectoryStream(folderPath, "*.json");

            for (Path filePath : jsonFiles) {
                String filename = filePath.getFileName().toString();

                try {
                    List<Question> questions = mapper.readValue(filePath.toFile(), new TypeReference<List<Question>>() {});

                    // Appliquer un temps par défaut si nécessaire
                    for (Question q : questions) {
                        if (q.getTimeLimitSeconds() <= 0) {
                            q.setTimeLimitSeconds(DEFAULT_TIME_LIMIT);
                        }
                    }

                    // Vérification du quiz
                    if (isValidQuizz(questions)) {
                        String quizzName = filename.replace(".json", "");
                        quizzes.put(quizzName, questions);
                    } else {
                        System.err.println("Fichier invalide : " + filename + " (quiz ignoré)");
                    }
                } catch (IOException e) {
                    System.err.println("Erreur lors du chargement de " + filename + " → ignoré.");
                }
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return quizzes;
    }

    /**
     * Vérifie que chaque question a un texte, des options valides,
     * un index de bonne réponse correct, et un temps > 0.
     */
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
