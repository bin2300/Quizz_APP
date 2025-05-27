package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuizCreator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuizCreationWindow {

    public static void display(Stage stage, Scene mainMenu) {
        Label title = new Label("Création de Quiz");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField quizNameField = new TextField();
        quizNameField.setPromptText("Nom du quiz");

        VBox questionsContainer = new VBox(10);

        Button addQuestionBtn = new Button("+ Ajouter une question");
        addQuestionBtn.setOnAction(e -> questionsContainer.getChildren().add(createQuestionBlock()));

        Button saveBtn = new Button("💾 Enregistrer le quiz");
        saveBtn.setOnAction(e -> {
            String quizName = quizNameField.getText().trim();
            if (quizName.isEmpty()) {
                showAlert("Le nom du quiz est requis.");
                return;
            }

            List<Question> questions = new ArrayList<>();
            for (var node : questionsContainer.getChildren()) {
                if (node instanceof VBox qBox) {
                    TextField qText = (TextField) qBox.getChildren().get(0);
                    TextField optA = (TextField) qBox.getChildren().get(1);
                    TextField optB = (TextField) qBox.getChildren().get(2);
                    TextField optC = (TextField) qBox.getChildren().get(3);
                    TextField optD = (TextField) qBox.getChildren().get(4);
                    ComboBox<String> correct = (ComboBox<String>) qBox.getChildren().get(5);
                    Spinner<Integer> timer = (Spinner<Integer>) qBox.getChildren().get(6);

                    if (qText.getText().isEmpty() || optA.getText().isEmpty() || optB.getText().isEmpty()
                            || optC.getText().isEmpty() || optD.getText().isEmpty()) {
                        showAlert("Toutes les questions doivent être complètes.");
                        return;
                    }

                    List<String> options = List.of(optA.getText(), optB.getText(), optC.getText(), optD.getText());
                    int correctIndex = correct.getSelectionModel().getSelectedIndex();
                    int timeLimit = timer.getValue();

                    questions.add(new Question(qText.getText(), options, correctIndex, timeLimit));
                }
            }

            QuizCreator.createQuiz(quizName, questions);
            showAlert("✅ Quiz enregistré avec succès.");
            stage.setScene(mainMenu);
        });

        Button backBtn = new Button("⬅ Retour");
        backBtn.setOnAction(e -> stage.setScene(mainMenu));

        VBox layout = new VBox(15, title, quizNameField, addQuestionBtn, questionsContainer, saveBtn, backBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 600, 600);
        stage.setScene(scene);
    }

    private static VBox createQuestionBlock() {
        TextField questionField = new TextField();
        questionField.setPromptText("Texte de la question");

        TextField opt1 = new TextField("Réponse A");
        TextField opt2 = new TextField("Réponse B");
        TextField opt3 = new TextField("Réponse C");
        TextField opt4 = new TextField("Réponse D");

        ComboBox<String> correctBox = new ComboBox<>();
        correctBox.getItems().addAll("A", "B", "C", "D");
        correctBox.getSelectionModel().selectFirst();

        Spinner<Integer> timerSpinner = new Spinner<>(5, 300, 30);

        VBox box = new VBox(5, questionField, opt1, opt2, opt3, opt4, correctBox, timerSpinner);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        return box;
    }

    private static void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}
