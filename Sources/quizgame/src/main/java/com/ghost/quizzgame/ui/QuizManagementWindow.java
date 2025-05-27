package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.service.QuizManagementService;
// import com.ghost.quizzgame.utils.QuizCreator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;

public class QuizManagementWindow {

    public static void display(Stage stage, Scene mainMenu) {
        Label title = new Label("Gestion des Quizz");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> quizList = new ListView<>();
        Set<String> quizzes = QuizManagementService.listAllQuizzes();
        quizList.getItems().addAll(quizzes);

        Button editButton = new Button("✏️ Modifier");
        Button deleteButton = new Button("🗑️ Supprimer");
        Button backButton = new Button("⬅ Retour");

        HBox actionButtons = new HBox(10, editButton, deleteButton);
        actionButtons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, quizList, actionButtons, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);

        // 🔙 Retour au menu
        backButton.setOnAction(e -> stage.setScene(mainMenu));

        // 🗑️ Supprimer un quiz
        deleteButton.setOnAction(e -> {
            String selectedQuiz = quizList.getSelectionModel().getSelectedItem();
            if (selectedQuiz != null) {
                QuizManagementService.deleteQuiz(selectedQuiz);
                quizList.getItems().remove(selectedQuiz);
                showAlert("Quiz supprimé avec succès.");
            }
        });

        // ✏️ Modifier un quiz
        editButton.setOnAction(e -> {
            String selectedQuiz = quizList.getSelectionModel().getSelectedItem();
            if (selectedQuiz != null) {
                List<Question> questions = QuizManagementService.loadQuiz(selectedQuiz);
                QuizEditorWindow.display(stage, scene, selectedQuiz, questions);
            }
        });
    }

    private static void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}
