package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.service.QuizService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class QuizWindow {

    private static Timeline timeline;
    private static QuizService service;

    public static void start(Stage stage, String quizName, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            showAlert("Erreur", "Le quiz est vide ou introuvable.");
            return;
        }

        service = new QuizService(quizName, questions);
        showQuestion(stage);
    }

    private static void showQuestion(Stage stage) {
        Question current = service.getCurrentQuestion();

        Label questionLabel = new Label(current.getQuestionText());
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ToggleGroup optionsGroup = new ToggleGroup();
        VBox optionsBox = new VBox(10);
        for (int i = 0; i < current.getOptions().size(); i++) {
            RadioButton option = new RadioButton(current.getOptions().get(i));
            option.setToggleGroup(optionsGroup);
            option.setUserData(i);
            optionsBox.getChildren().add(option);
        }

        Label timerLabel = new Label();
        int timeLimit = current.getTimeLimitSeconds();
        timerLabel.setText("⏳ Temps restant : " + timeLimit + "s");

        Button nextButton = new Button("Suivant");

        VBox layout = new VBox(20, questionLabel, optionsBox, timerLabel, nextButton);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
        stage.show();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int time = Integer.parseInt(timerLabel.getText().replaceAll("\\D+", ""));
            time--;
            timerLabel.setText("⏳ Temps restant : " + time + "s");

            if (time <= 0) {
                timeline.stop();
                handleAnswer(optionsGroup, stage);
            }
        }));
        timeline.setCycleCount(timeLimit);
        timeline.play();

        nextButton.setOnAction(e -> {
            timeline.stop();
            handleAnswer(optionsGroup, stage);
        });
    }

    private static void handleAnswer(ToggleGroup optionsGroup, Stage stage) {
        int selected = -1;
        if (optionsGroup.getSelectedToggle() != null) {
            selected = (int) optionsGroup.getSelectedToggle().getUserData();
        }

        service.saveUserAnswer(selected);

        if (service.isFinished()) {
            service.saveFinalResult();
            showResult(stage);
        } else {
            showQuestion(stage);
        }
    }

    private static void showResult(Stage stage) {
        Label resultLabel = new Label("Quiz terminé ! Score : " + service.getScore() + "/" + service.getTotalQuestions());
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox layout = new VBox(20, resultLabel);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(layout, 400, 200));
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
