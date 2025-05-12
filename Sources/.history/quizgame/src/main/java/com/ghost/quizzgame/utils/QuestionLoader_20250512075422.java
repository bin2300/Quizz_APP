package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class QuestionLoader {
    private static final String QUIZZ_FOLDER = "data";


    /*
     * # CHARGER TOUT LES FICHIER DU DOSSIER RESSOURCES
     * - considerer chaque json qui respecte le model defenie dans la classe question comme un quiz
     */

    public static Map<String, List<Question>> loadAllQuizzes(){
        Map<String , List<Question>> quizzes = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();


        try {
            URL folderUrl = QuestionLoader.class.getClassLoader().getResource(QUIZZ_FOLDER)
            
            if (folderUrl == null){
                System.err.println("Dossier 'data' introuvalble dans ressources ");
                return quizzes;
            }

            Path folderPath = Paths.get(folderUrl.toURI());
            DirectoryStream<Path> jsonFiles = Files.newDirectoryStream(folderPath, "*.json");


            for (Path filePath : jsonFiles) {
                String filename = filePath.getFileName().toString();

                try{
                    List<Question> questions = mapper.readValue(filePath.toFile(), new TypeReference<<>>() {
                        
                    } null)
                }catch(IOException e){
                    System.err.println("Erreur lors du chargement de " + filename + "-> ignoré");
                }
            }
   
        }catch(URISyntaxException | IOException e){
            System.err.println("Erreur lors du chargement de " + filename + "-> ignore");
        }

        return quizzes;
    }
}


