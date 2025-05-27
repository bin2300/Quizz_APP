package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class QuizCreator {

    private static final Path QUIZ_FOLDER = Paths.get("../generated_data");
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Crée un quiz avec les questions fournies.
     * Le fichier est écrit dans generated_data/[quizName].json
     */
    public static void createQuiz(String quizName, List<Question> questions) {
        try {
            if (!Files.exists(QUIZ_FOLDER)) {
                Files.createDirectories(QUIZ_FOLDER);
            }

            String safeQuizName = sanitizeName(quizName);
            Path quizFile = QUIZ_FOLDER.resolve(safeQuizName + ".json");

            mapper.writerWithDefaultPrettyPrinter().writeValue(quizFile.toFile(), questions);
            System.out.println("✅ Quiz créé : " + quizFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la création du quiz '" + quizName + "'");
            e.printStackTrace();
        }
    }

    /**
     * Met à jour un quiz existant.
     * Écrase le fichier correspondant si présent dans generated_data.
     */
    public static void updateQuiz(String quizName, List<Question> updatedQuestions) {
        String safeQuizName = sanitizeName(quizName);
        Path quizFile = QUIZ_FOLDER.resolve(safeQuizName + ".json");

        if (!Files.exists(quizFile)) {
            System.err.println("⚠️ Quiz '" + quizName + "' introuvable dans generated_data/ (impossible à modifier)");
            return;
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(quizFile.toFile(), updatedQuestions);
            System.out.println("✏️ Quiz '" + quizName + "' mis à jour.");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la mise à jour du quiz '" + quizName + "'");
            e.printStackTrace();
        }
    }

    /**
     * Supprime un quiz dans generated_data/.
     */
    public static void deleteQuiz(String quizName) {
        String safeQuizName = sanitizeName(quizName);
        Path quizFile = QUIZ_FOLDER.resolve(safeQuizName + ".json");

        if (Files.exists(quizFile)) {
            try {
                Files.delete(quizFile);
                System.out.println("🗑️ Quiz '" + quizName + "' supprimé avec succès.");
            } catch (IOException e) {
                System.err.println("❌ Erreur lors de la suppression du quiz '" + quizName + "'");
                e.printStackTrace();
            }
        } else {
            System.err.println("⚠️ Quiz '" + quizName + "' non trouvé dans generated_data/");
        }
    }

    /**
     * Nettoie le nom du quiz pour un usage sûr comme nom de fichier.
     */
    private static String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]", "_").toLowerCase();
    }
}
