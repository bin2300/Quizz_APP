package com.ghost.quizzgame;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;

import java.util.List;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        Map<String, List<Question>> quizzes = QuestionLoader.loadAllQuizzes();
        quizzes.forEach((name, questions)->{
            System.out.println("Quiz : " + name + "(")
        });
    }
}