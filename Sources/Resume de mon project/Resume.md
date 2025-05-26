## PROJECT D'APPLICATION DE QUIZ EN JAVA

Il s'agit d'un project d'ecole portant sur un quizapp en java , cette application doit me 
permettre de creer , d'importer et de jouer a des quiz 

voici ce qui a ete fair pour l'instant :

quizgame/src/main/java/com/ghost/quizzgame/model/Question.java
```java
package com.ghost.quizzgame.model;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private int correctIndex;
    private int timeLimitSeconds; // 🕒 Nouveau champ

    public Question() {
        // Nécessaire pour Jackson
    }

    public Question(String questionText, List<String> options, int correctIndex, int timeLimitSeconds) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    // Getters et setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }
}
```

quizgame/src/main/java/com/ghost/quizzgame/ui/Menu.java
```java
package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class Menu {
    public static void display(Stage stage) {
        stage.setTitle("Quiz Game");

        // Création du layout principal du menu
        Label title = new Label("Bienvenue dans le Quiz Game !");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button createQuizButton = new Button("Créer un quiz");
        Button takeQuizButton = new Button("Faire un quiz");

        VBox mainLayout = new VBox(20, title, createQuizButton, takeQuizButton);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Scene mainScene = new Scene(mainLayout, 400, 300);

        // Action du bouton "Créer un quiz"
        createQuizButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Module de création à venir");
            alert.showAndWait();
        });

        // Action du bouton "Faire un quiz"
        takeQuizButton.setOnAction(e -> {
            Map<String, List<Question>> quizzes = QuestionLoader.loadAllQuizzes();

            Label quizLabel = new Label("Choisissez un quiz :");
            quizLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ListView<String> listView = new ListView<>();
            listView.getItems().addAll(quizzes.keySet());

            Button backButton = new Button("⬅ Retour au menu");
            backButton.setOnAction(ev -> stage.setScene(mainScene));

            VBox quizListLayout = new VBox(15, quizLabel, listView, backButton);
            quizListLayout.setPadding(new Insets(20));
            quizListLayout.setAlignment(Pos.CENTER);

            Scene quizScene = new Scene(quizListLayout, 400, 400);
            stage.setScene(quizScene);

            // Quand un quiz est sélectionné
            listView.setOnMouseClicked(event -> {
                String selectedQuiz = listView.getSelectionModel().getSelectedItem();
                if (selectedQuiz != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Quiz sélectionné : " + selectedQuiz);
                    alert.showAndWait();
                    // Tu peux ici appeler une nouvelle vue de jeu
                }
            });
        });

        stage.setScene(mainScene);
        stage.show();
    }
}
```
quizgame/src/main/java/com/ghost/quizzgame/utils/QuestionLoader.java
```java
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
    private static final Path EXTERNAL_DATA_FOLDER = Paths.get("generated_data");
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

        // Charger depuis generated_data/
        if (Files.exists(EXTERNAL_DATA_FOLDER)) {
            try {
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
```
et 
quizgame/src/main/java/com/ghost/quizzgame/utils/QuizCreator.java
```java
package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class QuizCreator {

    private static final Path QUIZ_FOLDER = Paths.get("generated_data");
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
```

prend connaissance du code , pour que l'on puisse travailler

quizgame/src/main/resources/data/quiz_sample.json
```json
[
    {
      "questionText": "What is the capital of Germany?",
      "options": ["Berlin", "Munich", "Frankfurt", "Hamburg"],
      "correctIndex": 0,
      "timeLimitSeconds": 30
    },
    {
      "questionText": "Which number is even?",
      "options": ["3", "5", "6", "9"],
      "correctIndex": 2,
      "timeLimitSeconds": 20
    }
  ]
  
```