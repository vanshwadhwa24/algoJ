package com.algoj.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static com.algoj.ui.Kit.*;

/** All practice questions. Score is derived from the database: 10 points per distinct question answered correctly. */
public class PracticeView extends VBox {
    public PracticeView(Services svc, Runnable onScoreChange) {
        setSpacing(14);
        setPadding(new Insets(10, 0, 20, 0));
        Label score = label("", "muted");
        Runnable update = () -> score.setText(svc.quizzes().correctQuestionCount() * 10 + " pts earned  ·  "
                + svc.quizzes().correctQuestionCount() + "/" + svc.catalog().questions().size() + " correct");
        update.run();
        getChildren().addAll(label("Practice", "heading"), score);
        for (var q : svc.catalog().questions())
            getChildren().add(new QuestionCard(q, svc.quizzes(), () -> { update.run(); onScoreChange.run(); }));
    }
}
