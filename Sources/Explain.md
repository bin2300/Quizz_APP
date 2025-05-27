Bon pour le debugage , on va se passer de utils

donc fussionne :
```java
package com.ghost.quizzgame.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ai_Interaction {
    private static final String API_KEY = "AIzaSyBcaj5jfdgyLvJkLNTPQIuRocj63asdPV8"; // Remplace par ta clé réelle
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static String generateQuiz(String theme, int numberOfQuestions) throws IOException {
        String prompt = String.format(
                "Génère un quiz JSON de %d questions sur le thème '%s'. " +
                        "Le format doit être une liste JSON comme ceci : " +
                        "[{\"questionText\": \"...\", \"options\": [\"...\", \"...\", \"...\", \"...\"], " +
                        "\"correctIndex\": 0, \"timeLimitSeconds\": 30}]. " +
                        "Assure-toi que chaque question ait 4 options différentes, une réponse correcte (correctIndex), " +
                        "et une limite de temps raisonnable.",
                numberOfQuestions, theme
        );

        String payload = String.format(
                "{ \"contents\": [ { \"parts\": [ { \"text\": %s } ] } ] }",
                toJsonString(prompt)
        );

        System.out.println(payload);

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = connection.getResponseCode();
        InputStream responseStream = (status >= 200 && status < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        String response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(response);

        if (status != 200) {
            throw new IOException("Réponse invalide de l'IA : " + status + "\n" + response);
        }

        String jsonExtracted = extractJsonFromGeminiResponse(response);

        if (jsonExtracted == null) {
            throw new IOException("Réponse invalide de l'IA : aucun contenu utile.");
        }

        return jsonExtracted;
    }

    private static String toJsonString(String text) {
        return "\"" + text.replace("\"", "\\\"") + "\"";
    }

    private static String extractJsonFromGeminiResponse(String response) {
        try {
            // Essaye de capturer un bloc entre ```json ... ``` ou ``` ... ```
            Pattern markdownPattern = Pattern.compile("```(?:json)?\\s*(\\[.*?\\])\\s*```", Pattern.DOTALL);
            Matcher matcher = markdownPattern.matcher(response);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }

            // Fallback : capture directement un tableau JSON
            Pattern arrayPattern = Pattern.compile("(\\[\\s*\\{.*?\\}\\s*\\])", Pattern.DOTALL);
            matcher = arrayPattern.matcher(response);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }

            System.err.println("Aucun bloc JSON trouvé dans la réponse.");
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

```


et 
```java
package com.ghost.quizzgame.service;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.Ai_Interaction;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class AiQuizService {

    private static final Path AI_QUIZ_STORAGE = Paths.get("src/main/resources/generated_data/ai_quizzes.json");

    private final Gson gson;
    private final Map<String, List<Question>> aiQuizzes = new HashMap<>();

    public AiQuizService() {
        this.gson = new Gson();
        loadQuizzes();
    }

    /**
     * Génère un quiz IA selon le thème et le nombre de questions,
     * stocke localement le quiz et le sauvegarde dans le fichier JSON.
     */
    public String generateAiQuiz(String theme, int numberOfQuestions) throws IOException {
        if (numberOfQuestions < 1 || numberOfQuestions > 10) {
            throw new IllegalArgumentException("Le nombre de questions doit être entre 1 et 10.");
        }

        String quizJson = Ai_Interaction.generateQuiz(theme, numberOfQuestions);

        // Debug : affiche le JSON extrait
        System.out.println("JSON extrait de Gemini :\n" + quizJson);

        Type listType = new TypeToken<List<Question>>() {}.getType();
        List<Question> questions;

        try {
            questions = gson.fromJson(quizJson, listType);
        } catch (JsonSyntaxException e) {
            throw new IOException("Le JSON retourné par l'IA est invalide :\n" + quizJson, e);
        }

        if (questions == null || questions.isEmpty()) {
            throw new IOException("Le quiz IA généré est vide ou invalide.");
        }

        String formattedTheme = theme.replaceAll("[^a-zA-Z0-9_]", "_");
        String quizName = "AI_Quiz_" + formattedTheme + "_" + System.currentTimeMillis();

        aiQuizzes.put(quizName, questions);
        saveQuizzes();

        return quizName;
    }

    /**
     * Renomme un quiz IA existant.
     *
     * @param oldName nom actuel
     * @param newName nouveau nom souhaité
     * @return true si le renommage a réussi, false sinon
     */
    public boolean renameQuiz(String oldName, String newName) {
        if (!aiQuizzes.containsKey(oldName) || aiQuizzes.containsKey(newName)) {
            return false;
        }

        List<Question> questions = aiQuizzes.get(oldName);
        aiQuizzes.put(newName, questions);

        try {
            saveQuizzes();
            aiQuizzes.remove(oldName);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde après renommage : " + e.getMessage());
            aiQuizzes.remove(newName); // rollback
            return false;
        }
    }

    public List<Question> getQuizByName(String quizName) {
        return aiQuizzes.get(quizName);
    }

    public Set<String> getAllQuizNames() {
        return aiQuizzes.keySet();
    }

    private void loadQuizzes() {
        try {
            if (Files.exists(AI_QUIZ_STORAGE)) {
                String content = Files.readString(AI_QUIZ_STORAGE);
                Type type = new TypeToken<Map<String, List<Question>>>() {}.getType();
                Map<String, List<Question>> loaded = gson.fromJson(content, type);
                if (loaded != null) {
                    aiQuizzes.putAll(loaded);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des quiz IA : " + e.getMessage());
        }
    }

    private void saveQuizzes() throws IOException {
        if (!Files.exists(AI_QUIZ_STORAGE.getParent())) {
            Files.createDirectories(AI_QUIZ_STORAGE.getParent());
        }
        String json = gson.toJson(aiQuizzes);
        Files.writeString(AI_QUIZ_STORAGE, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

```


en un seul fichier qui sera : quizgame/src/main/java/com/ghost/quizzgame/service/AiQuizService.java

avec beaucoup de system.out.println et d'exeception pour voir l'erreur