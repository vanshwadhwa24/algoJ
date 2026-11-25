package com.algoj.ui;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.Algorithms;
import com.algoj.dto.ExecutionStep;
import com.algoj.ui.render.ArrayRenderers;
import javafx.geometry.Insets;
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
    private final TextField input = new TextField("35,12,48,22,60,5,41");
    private final ComboBox<String> pickA = new ComboBox<>(), pickB = new ComboBox<>();
    private final Canvas canvasA = new Canvas(560, 300), canvasB = new Canvas(560, 300);
    private final Label countA = label("", "badge"), countB = label("", "badge"), stepLabel = label("", "heading");
    private List<ExecutionStep> a = List.of(), b = List.of();

    public CompareView(Services svc) {
        this.svc = svc;
        setSpacing(16);
        setPadding(new Insets(10, 0, 20, 0));
        input.getStyleClass().add("text-input");
        input.setPrefWidth(320);
        for (var p : List.of(pickA, pickB)) {
            p.getItems().addAll(SORTS);
            p.setOnAction(e -> load());
        }
        pickA.setValue("bubble-sort");
        pickB.setValue("quick-sort");
        Button play = button("▶  Run Side-by-Side", "pill-btn", () -> { load(); playback.play(); });
        HBox top = row(12, label("Array", "muted"), input, play, button("Reset", "light-btn", this::load));
        HBox panels = row(16, panel("A", pickA, canvasA, countA), panel("B", pickB, canvasB, countB));
        panels.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        getChildren().addAll(label("Compare Algorithms", "heading"), card(top), panels, card(stepLabel));
        playback.indexProperty().addListener((o, x, y) -> draw());
        load();
    }

    private VBox panel(String name, ComboBox<String> pick, Canvas c, Label counter) {
        return card(row(10, label(name, "badge"), pick, grow(), counter), c);
    }

    private void load() {
        AlgorithmInput in = AlgorithmInput.ofArray(VisualizerView.parseInts(input.getText()), null);
        a = Algorithms.get(pickA.getValue()).run(in);
        b = Algorithms.get(pickB.getValue()).run(in);
        svc.runs().record(pickA.getValue() + " vs " + pickB.getValue(), input.getText(), Math.max(a.size(), b.size()));
        // Both traces advance together; the shorter one holds its final frame.
        playback.load(a.size() >= b.size() ? a : b);
        draw();
    }

    private void draw() {
        int i = playback.indexProperty().get();
        paint(canvasA, a, i, countA);
        paint(canvasB, b, i, countB);
        stepLabel.setText("Step " + (i + 1) + " of " + playback.total());
    }

    private void paint(Canvas c, List<ExecutionStep> steps, int i, Label counter) {
        var g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        if (steps.isEmpty()) return;
        ExecutionStep s = steps.get(Math.min(i, steps.size() - 1));
        new ArrayRenderers.Bars().draw(g, c.getWidth(), c.getHeight(), s.state());
        StringBuilder sb = new StringBuilder();
        if (s.counters() != null) s.counters().forEach((k, v) -> sb.append(sb.length() > 0 ? "  " : "").append(k).append(": ").append(v));
        counter.setText(sb.toString());
    }
}
