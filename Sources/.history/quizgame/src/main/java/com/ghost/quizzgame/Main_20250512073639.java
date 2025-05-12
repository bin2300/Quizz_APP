package com.ghost.quizzgame;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<Question> questions = QuestionLoader.loadQuestions("quiz_sample.json");
            for (Question q : questions){
                System.out.println("Q:" + q.getQuestionText());
                System.out.println("choices");
            }
        }
    }
}