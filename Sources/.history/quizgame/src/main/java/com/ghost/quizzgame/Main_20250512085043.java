package com.ghost.quizzgame;

import com.ghost.quizzgame.model.Question;
import com.ghost.quizzgame.utils.QuestionLoader;
import com.ghost.quizzgame.utils.QuizCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // === 1. Création d'un quiz pour la démo ===
        List<Question> newQuestions = new ArrayList<>();
        newQuestions.add(new Question("What is the largest planet?", List.of("Earth", "Jupiter", "Mars"), 1, 15));
        newQuestions.add(new Question("Which language runs in a browser?", List.of("Java", "Python", "JavaScript"), 2, 10));

        QuizCreator.createQuiz("space_and_web_basics", newQuestions);

        // === 2. Chargement de tous les quizzes ===
        Map<String, List<Question>> quizzes = QuestionLoader.loadAllQuizzes();

        if (quizzes.isEmpty()) {
            System.out.println("Aucun quiz valide trouvé.");
            return;
        }

        // === 3. Affichage ===
        System.out.println("📚 Liste des quiz détectés :\n");

        for (Map.Entry<String, List<Question>> entry : quizzes.entrySet()) {
            String quizName = entry.getKey();
            List<Question> questions = entry.getValue();

            System.out.println("==== 🧠 Quiz : " + quizName + " (" + questions.size() + " question(s)) ===");

            int qNumber = 1;
            for (Question question : questions) {
                System.out.println("\n" + qNumber + ". ❓ " + question.getQuestionText() +
                        " ⏱️ (" + question.getTimeLimitSeconds() + "s)");

                List<String> options = question.getOptions();

                for (int i = 0; i < options.size(); i++) {
                    String prefix = (i == question.getCorrectIndex()) ? "✅" : "⬜";
                    System.out.println("   " + prefix + " " + options.get(i));
                }

                qNumber++;
            }

            System.out.println("\n-----------------------------------------------------------------------\n");
        }
    }
}
