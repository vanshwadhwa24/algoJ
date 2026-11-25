package com.algoj.ui;

import com.algoj.catalog.AlgorithmInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;

import static com.algoj.ui.Kit.*;

/** Searchable, filterable grid of every algorithm and data structure. */
public class LibraryView extends VBox {
    private static final Map<String, String> CATEGORY_NAMES = Map.of("all", "All Categories", "sorting", "Sorting", "searching", "Searching",
            "linear", "Linear Structures", "trees-heaps", "Trees & Heaps", "graphs", "Graphs", "recursion", "Recursion",
            "dynamic-programming", "Dynamic Programming");
    private final List<AlgorithmInfo> all;
    private final Consumer<AlgorithmInfo> open;
    private final TextField search = new TextField();
    private final GridPane grid = new GridPane();
    private final HBox chips = new HBox(12);
    private final HBox difficultyChips = new HBox(8);
    private String category = "all";
    private String difficulty = "All";

    public LibraryView(Services svc, Consumer<AlgorithmInfo> open) {
        this.all = svc.catalog().algorithms();
        this.open = open;
        setSpacing(24);
        setPadding(new Insets(20, 0, 40, 0));
        grid.setHgap(24);
        grid.setVgap(24);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            grid.getColumnConstraints().add(cc);
        }

        Label title = label("Algorithm Library", "heading");
        title.setStyle("-fx-font-size:30px;-fx-font-weight:900;");
        Label sub = label("Browse through " + all.size() + " fully functional, interactive algorithms and data structures.", "heading");
        sub.setStyle("-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#4a443b;");
        search.setPromptText("⌕   Search by algorithm name, tag (e.g. In-Place, O(n log n), DP), or concept…");
        search.setStyle("-fx-background-color:#F7EFE1;-fx-background-radius:16;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:16;-fx-padding:14 18 14 18;-fx-font-size:14px;-fx-font-weight:600;");
        search.textProperty().addListener((o, a, b) -> render());
        List<String> cats = new ArrayList<>(List.of("all"));
        all.stream().map(AlgorithmInfo::category).distinct().forEach(cats::add);
        for (String c : cats) {
            Button b = new Button(CATEGORY_NAMES.getOrDefault(c, c));
            b.setUserData(c);
            b.setOnAction(e -> setCategory(c));
            chips.getChildren().add(b);
        }
        Label dl = label("▽  DIFFICULTY:", "heading");
        dl.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#4a443b;");
        difficultyChips.setAlignment(Pos.CENTER_LEFT);
        difficultyChips.getChildren().add(dl);
        for (String d : List.of("All", "Easy", "Medium", "Hard")) {
            Button b = new Button(d);
            b.setUserData(d);
            b.setOnAction(e -> { difficulty = d; styleDifficulty(); render(); });
            difficultyChips.getChildren().add(b);
        }
        Region line = new Region();
        line.setStyle("-fx-background-color:#E8DCC4;");
        line.setMinHeight(1);
        line.setMaxHeight(1);
        VBox header = new VBox(12, title, sub, search, chips, line, difficultyChips);
        header.setPadding(new Insets(28));
        header.setStyle("-fx-background-color:white;-fx-background-radius:28;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:28;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.16),20,0,0,6);");
        VBox.setMargin(chips, new Insets(8, 0, 0, 0));
        getChildren().addAll(header, grid);
        setCategory("all");
        styleDifficulty();
    }

    private void styleDifficulty() {
        for (var n : difficultyChips.getChildren()) {
            if (n.getUserData() == null) continue;
            boolean on = n.getUserData().equals(difficulty);
            n.setStyle("-fx-background-radius:14;-fx-border-radius:14;-fx-border-color:#2B2620;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:3 12 3 12;-fx-cursor:hand;-fx-background-color:" + (on ? "#DFA43C" : "white") + ";");
        }
    }

    public void setCategory(String c) {
        category = c;
        for (var n : chips.getChildren()) {
            boolean on = n.getUserData().equals(c);
            n.setStyle("-fx-background-radius:18;-fx-border-radius:18;-fx-border-color:#2B2620;-fx-border-width:2;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:6 16 6 16;-fx-cursor:hand;"
                    + "-fx-background-color:" + (on ? "#2B2620" : "#F7EFE1") + ";-fx-text-fill:" + (on ? "white" : "#2B2620") + ";");
        }
        render();
    }

    private void render() {
        grid.getChildren().clear();
        String q = search.getText() == null ? "" : search.getText().toLowerCase();
        int i = 0;
        for (AlgorithmInfo a : all) {
            if (!category.equals("all") && !a.category().equals(category)) continue;
            if (!difficulty.equals("All") && !a.difficulty().equals(difficulty)) continue;
            if (!q.isBlank() && !(a.name() + " " + a.description() + " " + a.tags()).toLowerCase().contains(q)) continue;
            grid.add(card(a), i % 3, i / 3);
            i++;
        }
    }

    private VBox card(AlgorithmInfo a) {
        Label cat = label(a.categoryLabel(), "heading");
        cat.setStyle("-fx-font-size:10px;-fx-font-weight:900;-fx-background-color:#F7EFE1;-fx-border-color:#2B2620;-fx-background-radius:10;-fx-border-radius:10;-fx-padding:2 9 2 9;");
        Label diff = label(a.difficulty(), "heading");
        diff.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-background-radius:12;-fx-padding:2 12 2 12;-fx-background-color:" + switch (a.difficulty()) {
            case "Easy" -> "#9CCBA2";
            case "Medium" -> "#DFA43C";
            default -> "#D97B54";
        } + ";");
        Label name = label(a.name(), "heading");
        name.setStyle("-fx-font-size:17px;-fx-font-weight:900;");
        Label desc = label(a.description(), "heading");
        desc.setStyle("-fx-font-size:12px;-fx-font-weight:500;-fx-text-fill:#4a443b;");
        desc.setWrapText(true);
        FlowPane tags = new FlowPane(6, 6);
        a.tags().stream().limit(4).forEach(t -> tags.getChildren().add(label(t, "tag")));
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Region line = new Region();
        line.setStyle("-fx-background-color:#F0E6D2;");
        line.setMinHeight(1);
        line.setMaxHeight(1);
        Button go = button("▶  Visualize", "pill-btn", () -> open.accept(a));
        go.setStyle("-fx-background-color:#9CCBA2;-fx-border-width:2;-fx-font-size:12px;-fx-font-weight:900;");
        Label cx = label(a.complexity().timeAverage(), "mono");
        cx.setStyle("-fx-font-size:12px;-fx-font-weight:700;");
        HBox bottom = row(8, cx, grow(), go);
        bottom.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(8, row(0, cat, grow(), diff), name, desc, tags, spacer, line, bottom);
        card.setPrefHeight(186 + 62);
        card.setStyle("-fx-background-color:white;-fx-background-radius:28;-fx-padding:22;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.18),20,0,0,8);");
        return card;
    }
}
