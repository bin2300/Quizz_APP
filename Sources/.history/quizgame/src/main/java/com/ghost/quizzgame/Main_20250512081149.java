package com.ghost.quizzgame;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;

import java.util.List;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        Map<String , List<Question> > quizzes = QuestionLoader.loadAllQuizzes();

        if (quizzes.isEmpty()){
            System.out.println("Aucun quiz valide trouvé");
            return ;
        }

        System.out.println("Liste des quiz detectés : \n");

        for (Map.Entry<String, List<Question>> entry : quizzes.entrySet()){
            String quizzName = entry.getKey();
            List<Question> questions = entry.getValue();

            System.out.println("==== //== :" + quizzName + " (" + questions.size())
        }
    }
}