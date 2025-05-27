package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ghost.quizzgame.model.Question;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Quizz_importation {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean importQuiz(File file) {
        try {
            List<Question> questions = mapper.readValue(
                file,
                mapper.getTypeFactory().constructCollectionType(List.class, Question.class)
            );

            if (questions == null || questions.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'importation", "Le quiz est vide ou invalide.");
                return false;
            }

            String fileName = file.getName();
            String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";

            File destDir = new File("../generated_data");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            File destFile = new File(destDir, baseName + extension);

            // Si fichier existe déjà, on ajoute la date et l'heure au nom
            if (destFile.exists()) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                destFile = new File(destDir, baseName + "_" + timestamp + extension);
            }

            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Quiz importé avec succès : " + destFile.getAbsolutePath());
            return true;

        } catch (UnrecognizedPropertyException e) {
            showAlert(Alert.AlertType.ERROR,
                "Erreur d'importation",
                "Le fichier JSON contient une propriété inconnue : " + e.getPropertyName() +
                "\nVeuillez vérifier le format du fichier.");
        } catch (JsonProcessingException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'importation", "Le fichier JSON est mal formé ou invalide.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'importation", "Impossible de lire ou sauvegarder le fichier : " + e.getMessage());
        }
        return false;
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
