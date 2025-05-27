package com.ghost.quizzgame.service;

import com.ghost.quizzgame.model.Question;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;

public class AiQuizService {

    private static final String PROMPT_TEMPLATE = "Génère un quiz JSON de %d questions sur le thème '%s'. " +
            "Le format doit être une liste JSON comme ceci : " +
            "[{\"questionText\": \"...\", \"options\": [\"...\",\"...\",\"...\",\"...\"], \"correctIndex\": 0, \"timeLimitSeconds\": 30}]. "
            +
            "Assure-toi que chaque question ait 4 options différentes, une réponse correcte, et une limite de temps raisonnable.";

    private static final Path GENERATED_FOLDER = Paths.get("../generated_data");
    private static final Path API_KEY_PATH = Paths.get("src/main/resources/config/api_key.txt");

    private final Gson gson = new Gson();
    private final String endpoint;

    public AiQuizService() {
        // Charge la clé API depuis config/api_key.txt
        String key = loadApiKey();
        this.endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
                + key;
    }

    /**
     * Génère puis écrit le quiz dans generated_data/[theme].json (ou
     * theme_timestamp.json).
     * 
     * @return le nom du fichier (sans chemin) sous lequel le quiz a été enregistré.
     */
    public String generateAndSaveQuiz(String theme, int count) throws IOException {
        System.out.println("⏳ Génération IA : thème=" + theme + ", nb=" + count);
        if (count < 1 || count > 10)
            throw new IllegalArgumentException("Le nombre de questions doit être entre 1 et 10.");

        // Appel API + extraction JSON
        String raw = callGemini(theme, count);
        System.out.println("✅ JSON brut:\n" + raw);

        // Désérialisation
        Type listType = new TypeToken<List<Question>>() {
        }.getType();
        List<Question> questions;
        try {
            questions = gson.fromJson(raw, listType);
        } catch (JsonSyntaxException e) {
            throw new IOException("❌ JSON invalide :\n" + raw, e);
        }
        if (questions == null || questions.isEmpty())
            throw new IOException("❌ Quiz vide ou invalide.");

        // Prépare dossier
        if (!Files.exists(GENERATED_FOLDER)) {
            Files.createDirectories(GENERATED_FOLDER);
        }

        // Nom de base
        String safe = theme.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        String fileName = safe + ".json";
        Path file = GENERATED_FOLDER.resolve(fileName);

        // Si existe, ajoute timestamp minute-près
        if (Files.exists(file)) {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            fileName = safe + "_" + ts + ".json";
            file = GENERATED_FOLDER.resolve(fileName);
        }

        // Écriture pretty-printed
        String jsonOut = gson.toJson(questions);
        Files.writeString(file, jsonOut, StandardOpenOption.CREATE_NEW);

        System.out.println("✅ Quiz enregistré dans : " + file);
        return fileName;
    }

    private String callGemini(String theme, int cnt) throws IOException {
        String prompt = String.format(PROMPT_TEMPLATE, cnt, theme);
        String payload = "{ \"contents\": [ { \"parts\": [ { \"text\": "
                + toJsonString(prompt) + " } ] } ] }";

        System.out.println("Payload → " + payload);
        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream in = status < 400 ? conn.getInputStream() : conn.getErrorStream();
        String resp = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Réponse API (HTTP " + status + "):\n" + resp);
        if (status != 200)
            throw new IOException("Erreur API (HTTP " + status + ")\n" + resp);

        // Unescape et extraction
        String cleaned = resp
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        // Markdown JSON
        Matcher m = Pattern.compile("```(?:json)?\\s*(\\[.*?\\])\\s*```", Pattern.DOTALL).matcher(cleaned);
        if (m.find()) {
            return m.group(1).trim();
        }
        // Fallback tableau
        m = Pattern.compile("(\\[\\s*\\{.*?\\}\\s*\\])", Pattern.DOTALL).matcher(cleaned);
        if (m.find()) {
            return m.group(1).trim();
        }
        throw new IOException("❌ Aucun JSON détecté dans la réponse de l'IA.");
    }

    private static String toJsonString(String txt) {
        return "\"" + txt.replace("\"", "\\\"") + "\"";
    }

    public boolean renameQuiz(String oldName, String newName) {
        Path oldPath = GENERATED_FOLDER.resolve(oldName + ".json");
        Path newPath = GENERATED_FOLDER.resolve(newName + ".json");

        File oldFile = oldPath.toFile();
        File newFile = newPath.toFile();

        System.out.println("Renommage : " + oldPath + " -> " + newPath);
        if (!oldFile.exists()) {
            System.out.println("❌ Ancien fichier introuvable !");
            return false;
        }
        if (newFile.exists()) {
            System.out.println("❌ Nouveau nom déjà pris !");
            return false;
        }

        return oldFile.renameTo(newFile);
    }

    private String loadApiKey() {
        try {
            return Files.readString(API_KEY_PATH).trim();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire la clé API", e);
        }
    }
}
