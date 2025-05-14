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
