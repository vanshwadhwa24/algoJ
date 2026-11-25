package com.algoj.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import static com.algoj.ui.Kit.*;

/** All practice questions in a narrow centered panel. Score comes from the database: 10 points per distinct question answered correctly. */
public class PracticeView extends HBox {
    public PracticeView(Services svc, Runnable onScoreChange) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20, 0, 40, 0));
        Label title = label("?  Practice & Knowledge Check", "heading");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:900;");
        Label score = label("", "heading");
        score.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-background-color:#F7EFE1;-fx-border-color:#E8DCC4;-fx-background-radius:14;-fx-border-radius:14;-fx-padding:4 12 4 12;");
        Runnable update = () -> score.setText(svc.quizzes().correctQuestionCount() * 10 + " Total Points");
        update.run();
        Region line = new Region();
        line.setStyle("-fx-background-color:#E8DCC4;");
        line.setMinHeight(1);
        line.setMaxHeight(1);
        VBox panel = new VBox(14, row(8, title, grow(), score), line);
        panel.setMaxWidth(830);
        panel.setPrefWidth(830);
        panel.setStyle("-fx-background-color:white;-fx-background-radius:28;-fx-border-color:#E8DCC4;-fx-border-width:2;-fx-border-radius:28;-fx-padding:20;");
        int n = 1;
        for (var q : svc.catalog().questions())
            panel.getChildren().add(new QuestionCard(n++, q, svc.quizzes(), () -> { update.run(); onScoreChange.run(); }));
        getChildren().add(panel);
    }
}
