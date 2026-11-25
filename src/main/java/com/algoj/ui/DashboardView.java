package com.algoj.ui;

import com.algoj.catalog.AlgorithmInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;

import static com.algoj.ui.Kit.*;

/** Landing screen: hero, live stats from the database, category cards and shortcuts to Compare and Practice. */
public class DashboardView extends VBox {
    public DashboardView(Services svc, Consumer<String> category, Runnable compare, Runnable practice) {
        setSpacing(18);
        setPadding(new Insets(10, 0, 20, 0));

        Label h1 = label("Master Algorithms\nStep by Step.", "heading");
        h1.setStyle("-fx-font-size:40px;-fx-text-fill:white;");
        Label sub = label("Interactive visualizations, a Java engine behind every step, and your progress saved locally.", "muted");
        sub.setStyle("-fx-text-fill:#FBF3E3;-fx-font-size:15px;");
        sub.setWrapText(true);
        VBox hero = new VBox(14, h1, sub, button("Explore Algorithms  →", "dark-btn", () -> category.accept("all")));
        hero.setStyle("-fx-background-color:#C25A35;-fx-background-radius:32;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:32;-fx-padding:36;");

        var all = svc.catalog().algorithms();
        HBox stats = row(14,
                stat("Topics", String.valueOf(all.size()), "#9CCBA2"),
                stat("Practice questions", String.valueOf(svc.catalog().questions().size()), "#DFA43C"),
                stat("Runs recorded", String.valueOf(svc.runs().count()), "#8FB5AD"),
                stat("Points earned", String.valueOf(svc.quizzes().correctQuestionCount() * 10), "#D97B54"));
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        Map<String, List<AlgorithmInfo>> byCat = new LinkedHashMap<>();
        all.forEach(a -> byCat.computeIfAbsent(a.category(), k -> new ArrayList<>()).add(a));
        FlowPane cats = new FlowPane(14, 14);
        byCat.forEach((c, list) -> {
            VBox card = card(label(list.get(0).categoryLabel(), "badge"), label(list.size() + " topics", "heading"),
                    label(String.join(", ", list.stream().limit(3).map(AlgorithmInfo::name).toList()) + (list.size() > 3 ? "…" : ""), "muted"));
            card.setPrefWidth(300);
            card.setOnMouseClicked(e -> category.accept(c));
            card.setStyle("-fx-cursor:hand;");
            cats.getChildren().add(card);
        });

        VBox cmp = card(label("Compare Algorithms", "heading"), label("Run two sorts side by side on the same input.", "muted"), button("Compare", "pill-btn", compare));
        VBox prac = card(label("Practice Quiz", "heading"), label("Test yourself and earn points.", "muted"), button("Practice", "pill-btn", practice));
        HBox ctas = row(14, cmp, prac);
        ctas.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        getChildren().addAll(hero, stats, label("Browse by category", "heading"), cats, ctas);
    }

    private static VBox stat(String name, String value, String color) {
        Label v = label(value, "heading");
        v.setStyle("-fx-font-size:30px;");
        VBox b = new VBox(4, v, label(name, "muted"));
        b.getStyleClass().add("card");
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle("-fx-border-width:2;-fx-border-color:#2B2620;-fx-background-color:" + color + ";");
        return b;
    }
}
