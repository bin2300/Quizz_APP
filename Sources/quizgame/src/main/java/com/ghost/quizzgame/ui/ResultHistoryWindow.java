package com.ghost.quizzgame.ui;

import com.ghost.quizzgame.service.ResultService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ResultHistoryWindow {

    public static void display(Stage stage) {
        List<Map<String, Object>> results = ResultService.getAllResults();

        Label title = new Label("📊 Historique des quiz");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox resultsBox = new VBox(10);
        resultsBox.setPadding(new Insets(15));

        if (results.isEmpty()) {
            resultsBox.getChildren().add(new Label("Aucun résultat enregistré."));
        } else {
            for (Map<String, Object> result : results) {
                String line = String.format("🧪 Quiz : %s | Score : %s/%s | 📅 %s",
                        result.get("quiz"),
                        result.get("score"),
                        result.get("total"),
                        result.get("timestamp"));
                resultsBox.getChildren().add(new Label(line));
            }
        }

        ScrollPane scrollPane = new ScrollPane(resultsBox);
        scrollPane.setFitToWidth(true);

        Button backButton = new Button("⬅ Retour");
        backButton.setOnAction(e -> Menu.display(stage));

        VBox layout = new VBox(20, title, scrollPane, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
    }
}
