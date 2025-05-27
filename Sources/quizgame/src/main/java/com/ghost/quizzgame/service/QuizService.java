package com.ghost.quizzgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class QuizService {
    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private final List<Integer> userAnswers = new ArrayList<>();
    private final String quizName;

    private static final Path TEMP_DIR = Paths.get("src/main/resources/temp");
    private static final Path RESULT_FILE = Paths.get("user_data/result.json");

    public QuizService(String quizName, List<Question> questions) {
        this.quizName = quizName;
        this.questions = questions;
    }

    /**
     * Retourne la question courante, ou lance une exception si on est à la fin.
     */
    public Question getCurrentQuestion() {
        if (currentIndex >= questions.size()) {
            throw new IndexOutOfBoundsException("Aucune question disponible à l'index " + currentIndex);
        }
        return questions.get(currentIndex);
    }

    public Question getQuestionByIndex(int index) {
        if (index < 0 || index >= questions.size()) {
            throw new IndexOutOfBoundsException("Index de question invalide : " + index);
        }
        return questions.get(index);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getScore() {
        return score;
    }



    /**
     * Sauvegarde la réponse de l'utilisateur et incrémente l'index de la question
     * courante.
     * Ne fait rien si le quiz est déjà terminé.
     */
    public void saveUserAnswer(int selectedIndex) {
        if (isFinished()) {
            System.err.println("Impossible d'enregistrer la réponse : quiz déjà terminé.");
            return;
        }

        userAnswers.add(selectedIndex);
        saveTempAnswer(currentIndex, selectedIndex);

        if (selectedIndex == getCurrentQuestion().getCorrectIndex()) {
            score++;
        }

        currentIndex++;
    }

    public boolean isFinished() {
        return currentIndex >= questions.size();
    }

    /**
     * Sauvegarde finale du résultat dans un fichier JSON.
     */
    public void saveFinalResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("quiz", quizName);
        result.put("score", score);
        result.put("total", questions.size());
        result.put("timestamp", new Date().toString());

        System.out.println("Début de la sauvegarde du résultat final...");
        System.out.println("Chemin du fichier résultats : " + RESULT_FILE.toAbsolutePath());

        try {
            if (!Files.exists(RESULT_FILE.getParent())) {
                System.out.println("Le dossier parent n'existe pas. Création du dossier : " + RESULT_FILE.getParent());
                Files.createDirectories(RESULT_FILE.getParent());
                System.out.println("Dossier créé.");
            } else {
                System.out.println("Le dossier parent existe déjà : " + RESULT_FILE.getParent());
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> results = new ArrayList<>();

            if (Files.exists(RESULT_FILE)) {
                System.out.println("Fichier result.json existe. Lecture des résultats existants...");
                results = mapper.readValue(RESULT_FILE.toFile(), List.class);
                System.out.println("Résultats existants lus : " + results.size() + " entrée(s).");
            } else {
                System.out.println("Fichier result.json n'existe pas. Création d'une nouvelle liste de résultats.");
            }

            System.out.println("Ajout du nouveau résultat : " + result);
            results.add(result);

            System.out.println("Écriture des résultats dans le fichier...");
            mapper.writerWithDefaultPrettyPrinter().writeValue(RESULT_FILE.toFile(), results);
            System.out.println("Sauvegarde terminée avec succès.");

        } catch (IOException e) {
            System.err.println("Erreur enregistrement résultat final : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveTempAnswer(int index, int answer) {
        try {
            if (!Files.exists(TEMP_DIR))
                Files.createDirectories(TEMP_DIR);
            Path file = TEMP_DIR.resolve("q" + index + ".tmp");
            Files.writeString(file, String.valueOf(answer));
        } catch (IOException e) {
            System.err.println("Erreur enregistrement réponse temp : " + e.getMessage());
        }
    }
}
