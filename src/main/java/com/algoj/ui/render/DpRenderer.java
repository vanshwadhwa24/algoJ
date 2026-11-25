package com.algoj.ui.render;

import com.algoj.model.RecursionAndDPModel.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

/** Dynamic programming table for 0/1 knapsack and LCS: current cell, its dependencies and the final result. */
public class DpRenderer implements StateRenderer {
    @Override
    public void draw(GraphicsContext g, double w, double h, Object state) {
        List<List<Integer>> table; List<String> rows = new ArrayList<>(), cols = new ArrayList<>();
        int curR, curC; List<List<Integer>> deps; String footer;
        if (state instanceof KnapsackState s) {
            table = s.getTable(); curR = s.getCurrentRow(); curC = s.getCurrentCol(); deps = s.getActiveDependencies();
            rows.add("0");
            for (KnapsackItem it : s.getItems()) rows.add(it.getName() + "(" + it.getWeight() + ")");
            for (int c = 0; c <= s.getCapacity(); c++) cols.add(String.valueOf(c));
            footer = s.getOptimalValue() > 0 && s.getMessage() != null && s.getMessage().contains("solved") ? "Best value " + s.getOptimalValue() + "  items " + s.getSelectedItems() : null;
        } else if (state instanceof LCSState s) {
            table = s.getTable(); curR = s.getCurrentRow(); curC = s.getCurrentCol(); deps = s.getActiveDependencies();
            rows.add("Ø"); for (char ch : s.getS1().toCharArray()) rows.add(String.valueOf(ch));
            cols.add("Ø"); for (char ch : s.getS2().toCharArray()) cols.add(String.valueOf(ch));
            footer = s.getLcsResult() != null && !s.getLcsResult().isEmpty() ? "LCS: " + s.getLcsResult() : null;
        } else return;
        int nr = table.size(), nc = nr == 0 ? 0 : table.get(0).size();
        double cell = Math.min(52, Math.min((w - 130) / (nc + 1), (h - 60) / (nr + 1))), x0 = (w - (nc + 1) * cell) / 2 + 20, y0 = 20;
        for (int c = 0; c < nc; c++) Draw.text(g, c < cols.size() ? cols.get(c) : "", x0 + (c + 1.5) * cell, y0 + cell / 2, Draw.mono(11), Draw.MUTED);
        for (int r = 0; r < nr; r++) {
            Draw.text(g, r < rows.size() ? rows.get(r) : "", x0 - 36, y0 + (r + 1.5) * cell, Draw.mono(10), Draw.MUTED);
            for (int c = 0; c < nc; c++) {
                boolean cur = r == curR && c == curC, dep = isDep(deps, r, c);
                Color fill = cur ? Draw.MUSTARD : dep ? Draw.TEAL : Draw.WHITE;
                Draw.box(g, x0 + (c + 1) * cell + 2, y0 + (r + 1) * cell + 2, cell - 4, cell - 4, 8, fill, Draw.CREAM_BORDER);
                Draw.text(g, String.valueOf(table.get(r).get(c)), x0 + (c + 1.5) * cell, y0 + (r + 1.5) * cell, Draw.mono(12), Draw.INK);
            }
        }
        if (footer != null) Draw.textLeft(g, footer, 12, h - 12, Draw.mono(13), Draw.INK);
    }

    private static boolean isDep(List<List<Integer>> deps, int r, int c) {
        if (deps == null) return false;
        for (List<Integer> d : deps) if (d.get(0) == r && d.get(1) == c) return true;
        return false;
    }
}
