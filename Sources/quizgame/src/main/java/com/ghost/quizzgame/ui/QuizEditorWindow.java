package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuizCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuizEditorWindow {

    /**
     * Crée un nouveau quiz (mode création).
     */
    public static void show(Stage stage) {
        display(stage, null, null, new ArrayList<>());
    }

    /**
     * Édite un quiz existant (mode édition).
     */
    public static void display(Stage stage, Scene previousScene, String quizName, List<Question> existingQuestions) {
        boolean isEditMode = quizName != null;

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField quizNameField = new TextField();
        quizNameField.setPromptText("Nom du quiz");
        if (isEditMode) {
            quizNameField.setText(quizName);
            quizNameField.setDisable(true); // Ne pas permettre de changer le nom lors de l'édition
        }

        ObservableList<Question> questionList = FXCollections.observableArrayList(existingQuestions);

        ListView<Question> questionListView = new ListView<>(questionList);
        questionListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Question q, boolean empty) {
                super.updateItem(q, empty);
                setText(empty || q == null ? null : q.getQuestionText());
            }
        });

        Button addQuestionBtn = new Button("➕ Ajouter une question");
        Button removeQuestionBtn = new Button("🗑️ Supprimer la question sélectionnée");
        Button saveQuizBtn = new Button("💾 " + (isEditMode ? "Mettre à jour" : "Créer") + " le quiz");
        Button backBtn = new Button("⬅ Retour");

        addQuestionBtn.setOnAction(e -> showAddQuestionDialog(questionList));
        removeQuestionBtn.setOnAction(e -> {
            Question selected = questionListView.getSelectionModel().getSelectedItem();
            if (selected != null) questionList.remove(selected);
        });

        saveQuizBtn.setOnAction(e -> {
            String name = quizNameField.getText().trim();
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Nom manquant", "Veuillez saisir un nom de quiz.");
                return;
            }
            if (questionList.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Aucune question", "Ajoutez au moins une question.");
                return;
            }

            if (isEditMode) {
                QuizCreator.updateQuiz(name, new ArrayList<>(questionList));
                showAlert(Alert.AlertType.INFORMATION, "✅ Quiz mis à jour", "Le quiz a été mis à jour avec succès.");
            } else {
                QuizCreator.createQuiz(name, new ArrayList<>(questionList));
                showAlert(Alert.AlertType.INFORMATION, "✅ Quiz créé", "Le quiz a été créé avec succès.");
            }

            if (previousScene != null) stage.setScene(previousScene);
        });

        backBtn.setOnAction(e -> {
            if (previousScene != null) stage.setScene(previousScene);
        });

        HBox btnBox = new HBox(10, addQuestionBtn, removeQuestionBtn, saveQuizBtn);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(
                new Label(isEditMode ? "Modifier le quiz :" : "Créer un nouveau quiz :"),
                quizNameField,
                new Label("Questions :"),
                questionListView,
                btnBox,
                backBtn
        );

        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    private static void showAddQuestionDialog(ObservableList<Question> questionList) {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter une question");

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TextField questionField = new TextField();
        questionField.setPromptText("Texte de la question");

        List<TextField> optionFields = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            TextField opt = new TextField();
            opt.setPromptText("Option " + i);
            optionFields.add(opt);
        }

        Spinner<Integer> correctIndexSpinner = new Spinner<>(1, 4, 1);
        correctIndexSpinner.setEditable(true);

        Spinner<Integer> timeSpinner = new Spinner<>(5, 300, 30);
        timeSpinner.setEditable(true);

        Button confirm = new Button("Ajouter");
        confirm.setOnAction(e -> {
            String text = questionField.getText().trim();
            List<String> options = new ArrayList<>();
            for (TextField tf : optionFields) {
                options.add(tf.getText().trim());
            }

            if (text.isEmpty() || options.stream().anyMatch(String::isEmpty)) {
                showAlert(Alert.AlertType.ERROR, "Champs incomplets", "Remplissez tous les champs.");
                return;
            }

            int correctIndex = correctIndexSpinner.getValue() - 1;
            int timeLimit = timeSpinner.getValue();

            if (correctIndex < 0 || correctIndex >= options.size()) {
                showAlert(Alert.AlertType.ERROR, "Index invalide", "L’index de la bonne réponse doit être entre 1 et 4.");
                return;
            }

            questionList.add(new Question(text, options, correctIndex, timeLimit));
            dialog.close();
        });

        box.getChildren().addAll(questionField, new Label("Options :"));
        box.getChildren().addAll(optionFields);
        box.getChildren().addAll(new Label("Réponse correcte (1-4) :"), correctIndexSpinner);
        box.getChildren().addAll(new Label("Temps limite (en secondes) :"), timeSpinner);
        box.getChildren().add(confirm);

        dialog.setScene(new Scene(box));
        dialog.show();
    }

    private static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
