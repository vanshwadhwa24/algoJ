package com.algoj.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

import static com.algoj.ui.Kit.*;

/** Landing screen: hero, live stats from the database, category cards and shortcuts to Compare and Practice. */
public class DashboardView extends VBox {
    private record Category(String id, String title, String color, String icon, String desc) {}

    private static final Category[] CATEGORIES = {
            new Category("sorting", "Sorting Algorithms", "#DFA43C", "≡", "Bubble, Selection, Insertion, Merge, Quick, and Heap sort with comparison & swap tracking."),
            new Category("searching", "Searching Algorithms", "#9CCBA2", "⌕", "Linear and Binary Search with eliminated interval highlights and sorted validation."),
            new Category("linear", "Linear Data Structures", "#8FB5AD", "▦", "Arrays, Singly/Doubly Linked Lists, Stacks, Queues, and Hash Tables with collision probing."),
            new Category("trees-heaps", "Trees & Heaps", "#D97B54", "⑂", "BST, Binary Heaps with dual synchronized views, self-balancing AVL trees, and Tries."),
            new Category("graphs", "Graph Algorithms", "#9CCBA2", "⚯", "BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, Kruskal, and Prim spanning trees."),
            new Category("recursion", "Recursion & DP", "#DFA43C", "⚙", "Factorial call stacks, Fibonacci 3 paradigms, Tower of Hanoi, N-Queens, 0/1 Knapsack, and LCS."),
    };

    public DashboardView(Services svc, Consumer<String> category, Runnable launchVisualizer, Runnable compare, Runnable practice) {
        setSpacing(44);
        setPadding(new Insets(24, 0, 40, 0));
        getChildren().addAll(hero(launchVisualizer, () -> category.accept("all")), stats(svc), categories(svc, category), callouts(compare, practice));
    }

    private Node hero(Runnable launch, Runnable library) {
        Label tag = label("✦  DETERMINISTIC STEPPING  •  RECORDED IN JAVA", "heading");
        tag.setStyle("-fx-text-fill:white;-fx-font-size:12px;-fx-background-color:rgba(255,255,255,0.15);-fx-background-radius:20;-fx-border-color:rgba(255,255,255,0.4);-fx-border-width:2;-fx-border-radius:20;-fx-padding:6 16 6 16;");
        Label l1 = label("Master Algorithms", "heading");
        l1.setStyle("-fx-text-fill:white;-fx-font-size:68px;-fx-font-weight:900;");
        Label l2 = label("Step by Step.", "heading");
        l2.setStyle("-fx-text-fill:#2B2620;-fx-font-size:68px;-fx-font-weight:900;-fx-background-color:#DFA43C;-fx-background-radius:14;-fx-padding:0 10 0 10;");
        Label tagline = label("An interactive learning platform where every sorting pass, tree rotation, and dynamic programming cell is rendered from a recorded Java execution trace with reversible stepping.", "muted");
        tagline.setStyle("-fx-text-fill:rgba(255,255,255,0.85);-fx-font-size:18px;-fx-font-weight:500;");
        tagline.setWrapText(true);
        tagline.setMaxWidth(640);
        Button launchBtn = button("▶  Launch Visualizer", "dark-btn", launch);
        launchBtn.setStyle("-fx-padding:14 28 14 28;-fx-font-size:15px;-fx-font-weight:800;");
        Button libBtn = button("Explore Library  →", "light-btn", library);
        libBtn.setStyle("-fx-padding:14 28 14 28;-fx-font-size:15px;-fx-font-weight:800;");
        VBox text = new VBox(22, tag, new VBox(0, l1, l2), tagline, row(16, launchBtn, libBtn));
        text.setAlignment(Pos.TOP_LEFT);
        text.setPadding(new Insets(56));
        l2.setMaxWidth(Region.USE_PREF_SIZE);
        tag.setMaxWidth(Region.USE_PREF_SIZE);

        Pane art = new Pane() {
            @Override protected double computePrefHeight(double w) { return 0; }
            @Override protected double computeMinHeight(double w) { return 0; }
        };
        art.setMouseTransparent(true);
        Circle ring = new Circle(1216 - 90, 12, 100, Color.TRANSPARENT);
        ring.setStroke(Color.rgb(255, 255, 255, 0.2));
        ring.setStrokeWidth(26);
        Circle teal = new Circle(1216 - 270, 168, 24, Color.web("#33706A"));
        javafx.scene.shape.Arc dome = new javafx.scene.shape.Arc(1216 - 216 - 0, 0, 64, 64, 0, 180);
        dome.setType(javafx.scene.shape.ArcType.ROUND);
        dome.setFill(Color.web("#DFA43C"));
        Rectangle sq = new Rectangle(1216 - 270 - 32, 0, 32, 32);
        sq.setFill(Color.rgb(255, 255, 255, 0.85));
        art.getChildren().addAll(ring, teal, dome, sq);
        StackPane hero = new StackPane(art, text);
        hero.setAlignment(Pos.TOP_LEFT);
        hero.setStyle("-fx-background-color:#C25A35;-fx-background-radius:32;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.28),36,0,0,16);");
        Rectangle clip = new Rectangle();
        clip.setArcWidth(64);
        clip.setArcHeight(64);
        clip.widthProperty().bind(hero.widthProperty());
        clip.heightProperty().bind(hero.heightProperty());
        art.setClip(clip);
        // anchor ornaments to the hero's right and bottom edges
        hero.heightProperty().addListener((o, a, h) -> { dome.setCenterY(h.doubleValue()); sq.setY(h.doubleValue() - 32); });
        hero.widthProperty().addListener((o, a, w) -> {
            double W = w.doubleValue();
            ring.setCenterX(W - 40); ring.setCenterY(10); teal.setCenterX(W - 224); dome.setCenterX(W - 160); sq.setX(W - 260);
        });
        return hero;
    }

    private Node stats(Services svc) {
        HBox r = row(16,
                stat(String.valueOf(svc.catalog().algorithms().size()), "Topics", "#DFA43C"),
                stat(String.valueOf(svc.catalog().questions().size()), "Practice Questions", "#9CCBA2"),
                stat(String.valueOf(svc.runs().count()), "Runs Recorded", "#8FB5AD"),
                stat(String.valueOf(svc.quizzes().correctQuestionCount() * 10), "Points Earned", "#D97B54"));
        r.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        return r;
    }

    private static VBox stat(String value, String name, String color) {
        Label v = label(value, "mono");
        v.setStyle("-fx-font-size:36px;-fx-font-weight:900;");
        Label n = label(name.toUpperCase(), "heading");
        n.setStyle("-fx-font-size:11px;-fx-font-weight:900;");
        VBox b = new VBox(2, v, n);
        b.setAlignment(Pos.CENTER);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:" + color + ";-fx-background-radius:24;-fx-padding:18;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.2),16,0,0,6);");
        return b;
    }

    private Node categories(Services svc, Consumer<String> open) {
        Label title = label("Curated DSA Categories", "heading");
        title.setStyle("-fx-font-size:28px;-fx-font-weight:900;");
        Label sub = label("Select any topic to begin exploring interactive step execution.", "muted");
        sub.setStyle("-fx-font-weight:700;-fx-font-size:13px;");
        Button viewAll = new Button("View All (" + svc.catalog().algorithms().size() + ")");
        viewAll.setStyle("-fx-background-color:transparent;-fx-underline:true;-fx-font-weight:900;-fx-cursor:hand;");
        viewAll.setOnAction(e -> open.accept("all"));
        HBox head = row(10, new VBox(2, title, sub), grow(), viewAll);
        head.setAlignment(Pos.CENTER_LEFT);
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(24);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            grid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < CATEGORIES.length; i++) {
            Category c = CATEGORIES[i];
            int count = (int) svc.catalog().algorithms().stream().filter(a -> a.category().equals(c.id()) || (c.id().equals("recursion") && a.category().equals("dynamic-programming"))).count();
            Label icon = label(c.icon(), "heading");
            icon.setStyle("-fx-background-color:white;-fx-background-radius:20;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:20;-fx-min-width:40;-fx-min-height:40;-fx-alignment:center;-fx-font-size:18px;");
            Label cnt = label(count + " Topics", "heading");
            cnt.setStyle("-fx-background-color:#2B2620;-fx-text-fill:white;-fx-background-radius:12;-fx-font-size:11px;-fx-font-weight:900;-fx-padding:3 10 3 10;");
            Label t = label(c.title(), "heading");
            t.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
            Label d = label(c.desc(), "heading");
            d.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#2B2620;");
            d.setWrapText(true);
            Label explore = label("Explore Category  →", "heading");
            explore.setStyle("-fx-font-size:12px;-fx-font-weight:900;");
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            VBox card = new VBox(8, row(0, icon, grow(), cnt), t, d, spacer, explore);
            card.setPrefHeight(224);
            card.setStyle("-fx-background-color:" + c.color() + ";-fx-background-radius:28;-fx-padding:24;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.2),16,0,0,6);");
            card.setOnMouseClicked(e -> open.accept(c.id()));
            grid.add(card, i % 3, i / 3);
        }
        return new VBox(24, head, grid);
    }

    private Node callouts(Runnable compare, Runnable practice) {
        HBox r = row(24,
                callout("SIDE-BY-SIDE ARENA", "#DFA43C", "Compare Two Algorithms In Real-Time",
                        "Feed the exact same unsorted array to two sorts simultaneously. Watch comparisons, swaps, and asymptotic efficiency unfold side-by-side.",
                        "Launch Comparison Mode", "-fx-background-color:#2B2620;-fx-text-fill:white;", compare),
                callout("INTERACTIVE QUIZ", "#9CCBA2", "Test Your Conceptual Understanding",
                        "Answer targeted questions on time complexities, recurrence relations, tree rotations, and edge cases. Score points saved in your local database.",
                        "Start Practice Quiz", "-fx-background-color:#9CCBA2;-fx-text-fill:#2B2620;", practice));
        r.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        return r;
    }

    private VBox callout(String tag, String tagColor, String title, String body, String btn, String btnStyle, Runnable action) {
        Label t = label(tag, "heading");
        t.setStyle("-fx-background-color:" + tagColor + ";-fx-background-radius:12;-fx-font-size:10px;-fx-font-weight:900;-fx-padding:2 10 2 10;");
        Label h = label(title, "heading");
        h.setStyle("-fx-font-size:20px;-fx-font-weight:900;");
        h.setWrapText(true);
        Label b = label(body, "heading");
        b.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#4a443b;");
        b.setWrapText(true);
        Button button = button(btn, "pill-btn", action);
        button.setStyle(btnStyle + "-fx-font-size:12px;-fx-font-weight:900;-fx-padding:10 20 10 20;-fx-background-radius:20;-fx-border-radius:20;-fx-border-color:#2B2620;-fx-border-width:2;");
        VBox v = new VBox(8, t, h, b, button);
        v.setStyle("-fx-background-color:white;-fx-background-radius:28;-fx-padding:24;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.22),20,0,0,8);");
        return v;
    }
}
