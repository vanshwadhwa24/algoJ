package com.algoj.ui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Small factory helpers so screens build styled nodes in one line. */
public final class Kit {
    private Kit() {}

    public static Label label(String text, String... styles) {
        Label l = new Label(text);
        l.getStyleClass().addAll(styles);
        return l;
    }

    public static Button button(String text, String style, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add(style);
        b.setOnAction(e -> action.run());
        return b;
    }

    public static VBox card(Node... children) {
        VBox v = new VBox(12, children);
        v.getStyleClass().add("card");
        return v;
    }

    public static HBox row(double gap, Node... children) {
        return new HBox(gap, children);
    }

    public static Region grow() {
        Region r = new Region();
        HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    public static String badgeColor(String difficulty) {
        return switch (difficulty) {
            case "Easy" -> "-fx-background-color:#9CCBA2;";
            case "Medium" -> "-fx-background-color:#DFA43C;";
            default -> "-fx-background-color:#D97B54;";
        };
    }

    public static Label difficultyBadge(String difficulty) {
        Label l = label(difficulty.toUpperCase(), "badge");
        l.setStyle(badgeColor(difficulty));
        return l;
    }
}
