package com.algoj.ui;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.Algorithms;
import com.algoj.catalog.AlgorithmInfo;
import com.algoj.dto.ExecutionStep;
import com.algoj.ui.render.ArrayRenderers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

import static com.algoj.ui.Kit.*;

/** Runs two sorting algorithms on the same input and steps both in lockstep, with live counters. */
public class CompareView extends VBox {
    private static final List<String> SORTS = List.of("bubble-sort", "selection-sort", "insertion-sort", "merge-sort", "quick-sort", "heap-sort");
    private final Playback playback = new Playback();
    private final Services svc;
    private final TextField input = new TextField("45, 12, 85, 32, 89, 39, 69, 44, 42, 1, 10");
    { input.setPromptText("shared array"); }
    private final ComboBox<AlgorithmInfo> pickA = new ComboBox<>(), pickB = new ComboBox<>();
    private final Canvas canvasA = new Canvas(560, 290), canvasB = new Canvas(560, 290);
    private final Label[] cmpA = stat(), cmpB = stat();
    private final Label worstA = label("", "mono"), worstB = label("", "mono");
    private List<ExecutionStep> a = List.of(), b = List.of();

    public CompareView(Services svc) {
        this.svc = svc;
        setSpacing(20);
        setPadding(new Insets(20, 0, 40, 0));
        input.setStyle("-fx-background-color:#F7EFE1;-fx-background-radius:14;-fx-border-color:#E8DCC4;-fx-border-radius:14;-fx-font-family:'JetBrains Mono';-fx-font-weight:700;-fx-font-size:12px;-fx-padding:6 12 6 12;");
        HBox.setHgrow(input, Priority.ALWAYS);
        List<AlgorithmInfo> infos = SORTS.stream().map(id -> svc.catalog().find(id).orElseThrow()).toList();
        for (var p : List.of(pickA, pickB)) {
            p.getItems().addAll(infos);
            p.setConverter(new javafx.util.StringConverter<>() {
                public String toString(AlgorithmInfo i) { return i == null ? "" : i.name(); }
                public AlgorithmInfo fromString(String s) { return null; }
            });
            p.setOnAction(e -> load());
            p.setStyle("-fx-font-size:13px;-fx-font-weight:800;-fx-background-radius:10;-fx-border-radius:10;");
        }
        pickA.setValue(infos.get(0));
        pickB.setValue(infos.get(4));

        Label title = label("⇄  Algorithm Comparison Arena", "heading");
        title.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
        Button run = new Button("▶  Run Side-by-Side");
        run.setStyle("-fx-background-color:#9CCBA2;-fx-background-radius:20;-fx-font-size:12px;-fx-font-weight:900;-fx-padding:9 18 9 18;-fx-cursor:hand;");
        run.setOnAction(e -> { load(); playback.play(); });
        Button reset = new Button("↻");
        reset.setStyle("-fx-background-color:white;-fx-border-color:#2B2620;-fx-border-width:1.5;-fx-border-radius:16;-fx-background-radius:16;-fx-font-weight:900;-fx-cursor:hand;");
        reset.setOnAction(e -> load());
        Button apply = new Button("Apply");
        apply.setStyle("-fx-background-color:#2B2620;-fx-text-fill:white;-fx-background-radius:10;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:6 16 6 16;-fx-cursor:hand;");
        apply.setOnAction(e -> load());
        Button random = new Button("Randomize");
        random.setStyle("-fx-background-color:#8FB5AD;-fx-background-radius:10;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:6 16 6 16;-fx-cursor:hand;");
        random.setOnAction(e -> {
            Random r = new Random();
            StringJoiner sj = new StringJoiner(", ");
            for (int i = 0; i < 10; i++) sj.add(String.valueOf(1 + r.nextInt(90)));
            input.setText(sj.toString());
            load();
        });
        Label shared = label("Shared Input Array:", "heading");
        shared.setStyle("-fx-font-size:11px;-fx-font-weight:900;-fx-text-fill:#4a443b;");
        HBox line1 = row(10, title, grow(), run, reset);
        line1.setAlignment(Pos.CENTER_LEFT);
        HBox line2 = row(10, shared, input, apply, random);
        line2.setAlignment(Pos.CENTER_LEFT);
        VBox top = new VBox(14, line1, line2);
        top.setStyle("-fx-background-color:white;-fx-background-radius:24;-fx-padding:18 20 18 20;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.16),16,0,0,6);");

        HBox panels = row(24, panel("A", "#DFA43C", pickA, canvasA, cmpA, worstA), panel("B", "#9CCBA2", pickB, canvasB, cmpB, worstB));
        panels.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        getChildren().addAll(top, panels);
        playback.indexProperty().addListener((o, x, y) -> draw());
        load();
    }

    private static Label[] stat() {
        return new Label[]{label("0", "mono"), label("0", "mono"), label("0", "mono")};
    }

    private VBox panel(String letter, String color, ComboBox<AlgorithmInfo> pick, Canvas c, Label[] s, Label worst) {
        Label dot = label(letter, "heading");
        dot.setStyle("-fx-background-color:" + color + ";-fx-border-color:#2B2620;-fx-background-radius:12;-fx-border-radius:12;-fx-font-size:10px;-fx-font-weight:900;-fx-min-width:22;-fx-min-height:22;-fx-alignment:center;");
        worst.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-border-color:#2B2620;-fx-border-radius:12;-fx-padding:3 10 3 10;");
        HBox head = row(10, dot, pick, grow(), worst);
        head.setAlignment(Pos.CENTER_LEFT);
        Region line = new Region();
        line.setStyle("-fx-background-color:#F0E6D2;");
        line.setMinHeight(1);
        line.setMaxHeight(1);
        StackPane inset = new StackPane(c);
        inset.setStyle("-fx-background-color:#F7EFE1;-fx-background-radius:20;-fx-border-color:#E8DCC4;-fx-border-radius:20;");
        inset.widthProperty().addListener((o, x, w) -> { c.setWidth(w.doubleValue()); });
        HBox stats = row(10, box("Comparisons", s[0]), box("Swaps", s[1]), box("Step", s[2]));
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        VBox v = new VBox(14, head, line, inset, stats);
        v.setStyle("-fx-background-color:white;-fx-background-radius:28;-fx-padding:20;-fx-effect:dropshadow(gaussian,rgba(43,38,32,0.18),20,0,0,8);");
        return v;
    }

    private VBox box(String name, Label value) {
        Label n = label(name, "heading");
        n.setStyle("-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#4a443b;");
        value.setStyle("-fx-font-size:15px;-fx-font-weight:900;");
        VBox b = new VBox(2, n, value);
        b.setAlignment(Pos.CENTER);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:#F7EFE1;-fx-border-color:#2B2620;-fx-border-radius:14;-fx-background-radius:14;-fx-padding:10;");
        return b;
    }

    private void load() {
        AlgorithmInput in = AlgorithmInput.ofArray(VisualizerView.parseInts(input.getText()), null);
        a = Algorithms.get(pickA.getValue().id()).run(in);
        b = Algorithms.get(pickB.getValue().id()).run(in);
        worstA.setText("Worst: " + pickA.getValue().complexity().timeWorst());
        worstB.setText("Worst: " + pickB.getValue().complexity().timeWorst());
        svc.runs().record(pickA.getValue().id() + " vs " + pickB.getValue().id(), input.getText(), Math.max(a.size(), b.size()));
        // Both traces advance together; the shorter one holds its final frame.
        playback.load(a.size() >= b.size() ? a : b);
        draw();
    }

    private void draw() {
        int i = playback.indexProperty().get();
        paint(canvasA, a, i, cmpA);
        paint(canvasB, b, i, cmpB);
    }

    private void paint(Canvas c, List<ExecutionStep> steps, int i, Label[] stats) {
        var g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        if (steps.isEmpty()) return;
        int at = Math.min(i, steps.size() - 1);
        ExecutionStep s = steps.get(at);
        new ArrayRenderers.Bars().draw(g, c.getWidth(), c.getHeight(), s.state());
        Map<String, Integer> k = s.counters() == null ? Map.of() : s.counters();
        stats[0].setText(String.valueOf(k.getOrDefault("comparisons", 0)));
        stats[1].setText(String.valueOf(k.getOrDefault("swaps", 0)));
        stats[2].setText((at + 1) + "/" + steps.size());
    }
}
