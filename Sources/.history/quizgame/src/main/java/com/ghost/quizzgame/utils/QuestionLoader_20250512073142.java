package com.ghost.quizzgame.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.quizzgame.model.Question;

import java.io.InputStream;

public class QuestionLoader {
    public static List<Question> loadQuestions(String filename) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = QuestionLoader.class.getClassLoader().getResourceAsStream("data/"+filename)
    }
}


