package com.algoj.ui;

import com.algoj.catalog.PracticeQuestion;
import com.algoj.persistence.QuizAttemptDao;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static com.algoj.ui.Kit.*;

/** One multiple-choice practice question. A correct answer is recorded through the DAO and scores 10 points once. */
public class QuestionCard extends VBox {
    public QuestionCard(PracticeQuestion q, QuizAttemptDao quizzes, Runnable onScoreChange) {
        getStyleClass().add("card");
        setSpacing(10);
        Label question = label(q.question(), "heading");
        question.setWrapText(true);
        getChildren().add(question);
        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < q.options().size(); i++) {
            RadioButton r = new RadioButton(q.options().get(i));
            r.setToggleGroup(group);
            r.setUserData(i);
            r.setWrapText(true);
            getChildren().add(r);
        }
        Label result = label("", "muted");
        result.setWrapText(true);
        Button check = button("Check Answer", "pill-btn", () -> {
            if (group.getSelectedToggle() == null) return;
            boolean ok = (int) group.getSelectedToggle().getUserData() == q.correctIndex();
            boolean firstTime = quizzes.record(q.id(), q.algorithmId(), ok);
            result.setText((ok ? (firstTime ? "Correct! +10 pts. " : "Correct. ") : "Not quite. ") + q.explanation());
            result.setStyle("-fx-text-fill:" + (ok ? "#33706A" : "#C25A35") + ";");
            onScoreChange.run();
        });
        getChildren().addAll(check, result);
        if (quizzes.isAnswered(q.id())) result.setText("Already answered correctly.");
    }
}
