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
        Button createQuizButton   = new Button("➕ Créer un quiz");
        Button manageQuizButton   = new Button("🛠️ Gérer les quiz");
        Button importQuizButton   = new Button("📂 Importer un quiz");
        Button takeQuizButton     = new Button("▶️ Faire un quiz");
        Button aiQuizButton       = new Button("🎲 Générer un quiz IA");
        Button historyButton      = new Button("🕓 Voir l'historique");

        VBox mainLayout = new VBox(20,
            title,
            createQuizButton,
            manageQuizButton,
            importQuizButton,
            takeQuizButton,
            aiQuizButton,
            historyButton
        );
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Scene mainScene = new Scene(mainLayout, 400, 400);

        // ➕ Créer un quiz
        createQuizButton.setOnAction(e ->
            QuizCreationWindow.display(stage, mainScene)
        );

        // 🛠️ Gérer les quiz
        manageQuizButton.setOnAction(e ->
            QuizManagementWindow.display(stage, mainScene)
        );

        // 📂 Importer un quiz
        importQuizButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Importer un quiz JSON");
            chooser.getExtensionFilters()
                   .add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                Quizz_importation.importQuiz(file);
            }
        });

        // ▶️ Faire un quiz
        takeQuizButton.setOnAction(e -> {
            Map<String, List<Question>> quizzes = QuestionLoader.loadAllQuizzes();
            Label prompt = new Label("Choisissez un quiz :");
            prompt.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            ListView<String> listView = new ListView<>();
            listView.getItems().addAll(quizzes.keySet());

            Button back = new Button("⬅ Retour au menu");
            back.setOnAction(ev -> stage.setScene(mainScene));

            VBox box = new VBox(15, prompt, listView, back);
            box.setPadding(new Insets(20));
            box.setAlignment(Pos.CENTER);

            Scene quizScene = new Scene(box, 400, 400);
            stage.setScene(quizScene);

            listView.setOnMouseClicked(event -> {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    List<Question> qs = quizzes.get(selected);
                    if (qs != null && !qs.isEmpty()) {
                        QuizWindow.start(stage, selected, qs, mainScene);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Quiz vide ou introuvable.").showAndWait();
                    }
                }
            });
        });

        // 🎲 Générer un quiz IA
        aiQuizButton.setOnAction(e -> {
            AiQuizGeneratorUI aiUI = new AiQuizGeneratorUI();
            aiUI.show(stage, mainScene);
        });

        // 🕓 Voir l'historique
        historyButton.setOnAction(e ->
            ResultHistoryWindow.display(stage)
        );

        stage.setScene(mainScene);
        stage.show();
    }
}
