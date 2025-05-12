package com.ghost.quizzgame;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, List<Question>> quizzes = QuestionLoader.loadAllQuizzes();

        if (quizzes.isEmpty()) {
            System.out.println("Aucun quiz valide trouvé.");
            return;
        }

        System.out.println("📚 Liste des quiz détectés :\n");

        for (Map.Entry<String, List<Question>> entry : quizzes.entrySet()) {
            String quizName = entry.getKey();
            List<Question> questions = entry.getValue();

            System.out.println("=== 📘 " + quizName + " (" + questions.size() + " question(s)) ===");

            int qNumber = 1;
            for (Question question : questions) {
                System.out.println("\n" + qNumber + ". ❓ " + question.getQuestionText());

                List<String> options = question.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    String prefix = (i == question.getCorrectIndex()) ? "✅" : "⬜";
                    System.out.println("   " + prefix + " " + options.get(i));
                }

                qNumber++;
            }

            System.out.println("\n--------------------------------------------\n");
        }
    }
}
