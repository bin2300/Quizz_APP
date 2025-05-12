package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

public class QuizCreator {

    private static final String QUIZ_FOLDER = "data";

    /**
     * Crée un quiz à partir d'une liste de questions.
     * Le fichier sera enregistré dans resources/data/[quizName].json
     */
    public static void createQuiz(String quizName, List<Question> questions) {
        try {
            // 1. Récupération du chemin vers /resources/data
            URL resourceRoot = QuizCreator.class.getClassLoader().getResource(".");
            if (resourceRoot == null) {
                System.err.println("Impossible d'accéder au dossier resources.");
                return;
            }

            Path resourcesPath = Paths.get(resourceRoot.toURI());
            Path dataDir = resourcesPath.resolve(QUIZ_FOLDER);

            // 2. Création du dossier /data s'il n'existe pas
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            // 3. Création du fichier de destination
            String safeQuizName = quizName.replaceAll("[^a-zA-Z0-9-_]", "_").toLowerCase(); // sécurité nom
            Path quizFile = dataDir.resolve(safeQuizName + ".json");

            // 4. Sérialisation avec Jackson
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(quizFile.toFile(), questions);

            System.out.println("✅ Quiz '" + quizName + "' créé avec succès : " + quizFile.toAbsolutePath());

        } catch (URISyntaxException | IOException e) {
            System.err.println("❌ Erreur lors de la création du quiz '" + quizName + "'");
            e.printStackTrace();
        }
    }
}
