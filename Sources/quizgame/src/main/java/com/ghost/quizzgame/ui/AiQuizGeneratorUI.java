package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.service.AiQuizService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AiQuizGeneratorUI {

    private final AiQuizService aiQuizService;

    public AiQuizGeneratorUI() {
        this.aiQuizService = new AiQuizService();
    }

    public void show(Stage stage, Scene mainScene) {
        Label titleLabel = new Label("Générateur de quiz IA");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label themeLabel = new Label("Thème du quiz :");
        TextField themeField = new TextField();
        themeField.setPromptText("Exemple : cybersécurité");

        Label numberLabel = new Label("Nombre de questions (1-10) :");
        Spinner<Integer> numberSpinner = new Spinner<>(1, 10, 5);
        numberSpinner.setEditable(true);

        Label nameLabel = new Label("Nom du fichier (optionnel) :");
        TextField nameField = new TextField();
        nameField.setPromptText("Sinon thème_timestamp.json");

        Button generateButton = new Button("Générer le quiz IA");
        Label feedbackLabel = new Label();

        Button backButton = new Button("⬅ Retour au menu");
        backButton.setOnAction(e -> stage.setScene(mainScene));

        generateButton.setOnAction(e -> {
            String theme = themeField.getText().trim();
            String customFile = nameField.getText().trim();
            feedbackLabel.setText("");
            generateButton.setDisable(true);
            generateButton.setText("Génération en cours...");

            int numQuestions;
            try {
                numberSpinner.commitValue();
                numQuestions = numberSpinner.getValue();
                if (numQuestions < 1 || numQuestions > 10) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Le nombre de questions doit être entre 1 et 10.");
                resetButton(generateButton);
                return;
            }

            if (theme.isEmpty()) {
                showAlert("Erreur", "Le thème ne peut pas être vide.");
                resetButton(generateButton);
                return;
            }

            new Thread(() -> {
                try {
                    // Appel de la nouvelle méthode
                    String fileName = aiQuizService.generateAndSaveQuiz(theme, numQuestions);

                    // Si on a précisé un nom, on renomme le fichier
                    if (!customFile.isEmpty() && !customFile.equals(fileName)) {
                        boolean ok = aiQuizService.renameQuiz(fileName, customFile);
                        if (!ok) throw new Exception("Impossible de renommer le fichier.");
                        fileName = customFile;
                    }

                    final String displayed = fileName;
                    javafx.application.Platform.runLater(() -> {
                        feedbackLabel.setStyle("-fx-text-fill: green;");
                        feedbackLabel.setText("Quiz enregistré sous : " + displayed);
                        resetButton(generateButton);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        showAlert("Erreur", "Échec : " + ex.getMessage());
                        feedbackLabel.setStyle("-fx-text-fill: red;");
                        feedbackLabel.setText("");
                        resetButton(generateButton);
                    });
                }
            }).start();
        });

        VBox root = new VBox(15,
            titleLabel,
            themeLabel, themeField,
            numberLabel, numberSpinner,
            generateButton,
            feedbackLabel,
            backButton
        );
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(420);

        stage.setScene(new Scene(root));
        stage.setTitle("Générateur Quiz IA");
        stage.show();

        themeField.requestFocus();
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void resetButton(Button btn) {
        btn.setDisable(false);
        btn.setText("Générer le quiz IA");
    }
}
