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
    // public static List<Question> loadQuestions(String filename) throws Exception{
    //     ObjectMapper mapper = new ObjectMapper();
    //     InputStream is = QuestionLoader.class.getClassLoader().getResourceAsStream("data/"+filename);
    //     return mapper.readValue(is, new TypeReference<List<Question>>(){});
    // }


    /*
     * # CHARGER TOUT LES FICHIER DU DOSSIER RESSOURCES
     * - considerer chaque json qui respecte le model defenie dans la classe question comme un quiz
     */

    public static Map<String, List<Question>> loadAllQuizzes(){
        Map<String , List<Question>> quizzes = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();


        try {

        }catch(URISyntaxException | IOException e){
            
        }
    }
}


