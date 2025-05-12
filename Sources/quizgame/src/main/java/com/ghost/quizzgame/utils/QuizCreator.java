package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class QuizCreator {

    // Répertoire en dehors de src/ pour que ce soit persistant
    private static final Path DATA_DIR = Paths.get("generated_data");

    public static void createQuiz(String quizName, List<Question> questions) {
        try {
            // 1. Créer le dossier s’il n’existe pas
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }

            // 2. Nom de fichier safe
            String safeQuizName = quizName.replaceAll("[^a-zA-Z0-9-_]", "_").toLowerCase();
            Path quizFile = DATA_DIR.resolve(safeQuizName + ".json");

            // 3. Écriture JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(quizFile.toFile(), questions);

            System.out.println("✅ Quiz '" + quizName + "' sauvegardé dans : " + quizFile.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la création du quiz '" + quizName + "'");
            e.printStackTrace();
        }
    }
}
