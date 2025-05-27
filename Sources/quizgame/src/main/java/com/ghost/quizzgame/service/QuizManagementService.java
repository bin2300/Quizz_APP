package com.ghost.quizzgame.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class QuizManagementService {
    private static final Path QUIZ_FOLDER = Paths.get("../generated_data");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Set<String> listAllQuizzes() {
        Set<String> quizNames = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(QUIZ_FOLDER, "*.json")) {
            for (Path path : stream) {
                String name = path.getFileName().toString().replaceFirst("[.][^.]+$", "");
                quizNames.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quizNames;
    }

    public static List<Question> loadQuiz(String quizName) {
        String safeName = sanitizeName(quizName);
        Path file = QUIZ_FOLDER.resolve(safeName + ".json");
        try {
            return mapper.readValue(file.toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void deleteQuiz(String quizName) {
        String safeName = sanitizeName(quizName);
        Path file = QUIZ_FOLDER.resolve(safeName + ".json");
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]", "_").toLowerCase();
    }
}
