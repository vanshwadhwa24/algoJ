package com.algoj.ui;

import com.algoj.catalog.PracticeQuestion;
import com.algoj.persistence.QuizAttemptDao;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

import static com.algoj.ui.Kit.*;

/** One multiple-choice practice question on a cream card with boxed full-width options. A correct answer scores 10 points once. */
public class QuestionCard extends VBox {
    private static final String OPTION = "-fx-background-color:white;-fx-background-radius:12;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:12;-fx-padding:9 14 9 14;-fx-font-size:12px;-fx-font-weight:700;-fx-alignment:center-left;-fx-cursor:hand;";

    public QuestionCard(int number, PracticeQuestion q, QuizAttemptDao quizzes, Runnable onScoreChange) {
        setSpacing(10);
        setStyle("-fx-background-color:#F7EFE1;-fx-background-radius:20;-fx-border-color:#E8DCC4;-fx-border-width:2;-fx-border-radius:20;-fx-padding:18;");
        Label num = label("Q" + number + ":", "heading");
        num.setStyle("-fx-text-fill:#6B6257;-fx-font-size:13px;-fx-font-weight:800;");
        Label question = label(q.question(), "heading");
        question.setStyle("-fx-font-size:13px;-fx-font-weight:800;");
        question.setWrapText(true);
        HBox head = row(6, num, question);
        HBox.setHgrow(question, Priority.ALWAYS);
        getChildren().add(head);

        ToggleGroup group = new ToggleGroup();
        List<ToggleButton> options = new ArrayList<>();
        for (int i = 0; i < q.options().size(); i++) {
            ToggleButton o = new ToggleButton(q.options().get(i));
            o.setToggleGroup(group);
            o.setUserData(i);
            o.setMaxWidth(Double.MAX_VALUE);
            o.setStyle(OPTION);
            o.selectedProperty().addListener((obs, a, on) -> o.setStyle(on ? OPTION + "-fx-background-color:#DFA43C;" : OPTION));
            options.add(o);
            getChildren().add(o);
        }
        Label result = label("", "heading");
        result.setStyle("-fx-font-size:12px;-fx-font-weight:600;");
        result.setWrapText(true);
        Button check = new Button("Check Answer");
        check.setStyle("-fx-background-color:#2B2620;-fx-text-fill:white;-fx-background-radius:18;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:8 18 8 18;-fx-cursor:hand;");
        check.setOnAction(e -> {
            if (group.getSelectedToggle() == null) return;
            int picked = (int) group.getSelectedToggle().getUserData();
            boolean ok = picked == q.correctIndex();
            boolean firstTime = quizzes.record(q.id(), q.algorithmId(), ok);
            options.get(q.correctIndex()).setStyle(OPTION + "-fx-background-color:#9CCBA2;");
            if (!ok) options.get(picked).setStyle(OPTION + "-fx-background-color:#D97B54;");
            result.setText((ok ? (firstTime ? "Correct! +10 pts. " : "Correct. ") : "Not quite. ") + q.explanation());
            onScoreChange.run();
        });
        HBox foot = row(10, check);
        foot.setAlignment(Pos.CENTER_RIGHT);
        getChildren().addAll(result, foot);
        if (quizzes.isAnswered(q.id())) {
            options.get(q.correctIndex()).setStyle(OPTION + "-fx-background-color:#9CCBA2;");
            result.setText("Already answered correctly. " + q.explanation());
        }
    }
}
