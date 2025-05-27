package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;
import com.ghost.quizzgame.utils.Quizz_importation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Menu {
    public static void display(Stage stage) {
        stage.setTitle("Quiz Game");

        // Titre principal
        Label title = new Label("Bienvenue dans le Quiz Game !");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Boutons
        Button createQuizButton = new Button("➕ Créer un quiz");
        Button manageQuizButton = new Button("🛠️ Gérer les quiz");
        Button importQuizButton = new Button("📂 Importer un quiz");
        Button takeQuizButton = new Button("▶️ Faire un quiz");
        Button historyButton = new Button("🕓 Voir l'historique");

        VBox mainLayout = new VBox(20, title, createQuizButton, manageQuizButton, importQuizButton, takeQuizButton, historyButton);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Scene mainScene = new Scene(mainLayout, 400, 350);

        // ➕ Créer un quiz
        createQuizButton.setOnAction(e -> QuizCreationWindow.display(stage, mainScene));

        // 🛠️ Gérer les quiz
        manageQuizButton.setOnAction(e -> QuizManagementWindow.display(stage, mainScene));

        // 📂 Importer un quiz
        importQuizButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importer un quiz JSON");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                Quizz_importation.importQuiz(selectedFile);
                // L'alerte est gérée dans importQuiz, donc pas besoin d'en afficher une ici
            }
        });

        // ▶️ Faire un quiz
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

            listView.setOnMouseClicked(event -> {
                String selectedQuiz = listView.getSelectionModel().getSelectedItem();
                if (selectedQuiz != null) {
                    List<Question> selectedQuestions = quizzes.get(selectedQuiz);
                    if (selectedQuestions != null && !selectedQuestions.isEmpty()) {
                        QuizWindow.start(stage, selectedQuiz, selectedQuestions, mainScene);
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Erreur : Quiz vide ou introuvable.");
                        error.showAndWait();
                    }
                }
            });
        });

        // 🕓 Voir l'historique
        historyButton.setOnAction(e -> ResultHistoryWindow.display(stage));

        stage.setScene(mainScene);
        stage.show();
    }
}
