package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;


public class QuizCreator{
    private static  final String QUIZ_FOLDER = "data";

    /***
     * Crée un quiz a partir d'une liste de question
     * Le fichier sera enregistré selon le modele
     */


    public static void create_QUIZ(String quizName, List<Question> questions){
        try{

        } catch (URISyntaxException | IOException e){
            System.err.println("[ Erreur ] Erreur lors de la creation du quiz `" + quizName)
        }
    }
}