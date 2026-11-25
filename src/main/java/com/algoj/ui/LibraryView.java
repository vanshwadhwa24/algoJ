package com.algoj.ui;

import com.algoj.catalog.AlgorithmInfo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;

import static com.algoj.ui.Kit.*;

/** Searchable, filterable grid of every algorithm and data structure. */
public class LibraryView extends VBox {
    private final List<AlgorithmInfo> all;
    private final Consumer<AlgorithmInfo> open;
    private final TextField search = new TextField();
    private final FlowPane grid = new FlowPane(16, 16);
    private final HBox chips = new HBox(8);
    private String category = "all";
    private String difficulty = "All";

    public LibraryView(Services svc, Consumer<AlgorithmInfo> open) {
        this.all = svc.catalog().algorithms();
        this.open = open;
        setSpacing(16);
        setPadding(new Insets(10, 0, 20, 0));
        search.setPromptText("Search algorithms, tags…");
        search.getStyleClass().add("text-input");
        search.setPrefWidth(380);
        search.textProperty().addListener((o, a, b) -> render());
        ComboBox<String> diff = new ComboBox<>();
        diff.getItems().addAll("All", "Easy", "Medium", "Hard");
        diff.setValue("All");
        diff.setOnAction(e -> { difficulty = diff.getValue(); render(); });
        HBox top = row(12, search, diff);
        getChildren().addAll(label("Algorithm Library", "heading"), top, chips, grid);
        List<String> cats = new ArrayList<>(List.of("all"));
        all.stream().map(AlgorithmInfo::category).distinct().forEach(cats::add);
        for (String c : cats) {
            Button b = new Button(c.equals("all") ? "ALL" : c.toUpperCase());
            b.setUserData(c);
            b.setOnAction(e -> setCategory(c));
            chips.getChildren().add(b);
        }
        setCategory("all");
    }

    public void setCategory(String c) {
        category = c;
        for (var n : chips.getChildren()) {
            n.getStyleClass().removeAll("chip", "chip-active");
            n.getStyleClass().add(n.getUserData().equals(c) ? "chip-active" : "chip");
        }
        render();
    }

    private void render() {
        grid.getChildren().clear();
        String q = search.getText() == null ? "" : search.getText().toLowerCase();
        for (AlgorithmInfo a : all) {
            if (!category.equals("all") && !a.category().equals(category)) continue;
            if (!difficulty.equals("All") && !a.difficulty().equals(difficulty)) continue;
            if (!q.isBlank() && !(a.name() + " " + a.description() + " " + a.tags()).toLowerCase().contains(q)) continue;
            Label cat = label(a.categoryLabel(), "badge");
            cat.setStyle("-fx-background-color:#8FB5AD;");
            HBox badges = row(6, cat, difficultyBadge(a.difficulty()));
            Label desc = label(a.description(), "muted");
            desc.setWrapText(true);
            desc.setMinHeight(60);
            desc.setMaxHeight(60);
            FlowPane tags = new FlowPane(6, 6);
            a.tags().stream().limit(3).forEach(t -> tags.getChildren().add(label(t, "tag")));
            VBox card = card(badges, label(a.name(), "heading"), desc, tags,
                    row(8, label("⏱ " + a.complexity().timeAverage(), "mono"), grow(), button("Visualize  →", "pill-btn", () -> open.accept(a))));
            card.setPrefWidth(330);
            grid.getChildren().add(card);
        }
    }
}
