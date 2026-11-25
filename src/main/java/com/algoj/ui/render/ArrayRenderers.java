package com.algoj.ui.render;

import com.algoj.model.LinearStructureModel.ArrayCell;
import com.algoj.model.LinearStructureModel.ArrayDSState;
import com.algoj.model.SearchModel.SearchElement;
import com.algoj.model.SearchModel.SearchState;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import com.algoj.model.TreeStructureModel.HeapDSState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

/** Bars for sorting and searching, cells for the array structure, and the dual array+tree view of a heap. */
public final class ArrayRenderers {
    private ArrayRenderers() {}

    /** Value bars with role badges (COMPARE, SWAP, PIVOT, MID, FOUND) and LOW/HIGH pointers. */
    public static class Bars implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            List<int[]> bars = new ArrayList<>();
            List<String> states = new ArrayList<>();
            List<Integer> comparing = List.of(), swapping = List.of(), sorted = List.of();
            Integer pivot = null, low = null, high = null, mid = null, current = null, found = null;
            if (state instanceof SortState s) {
                for (SortElement e : s.getArray()) { bars.add(new int[]{e.getValue()}); states.add(e.getState()); }
                comparing = s.getComparingIndices(); swapping = s.getSwappingIndices(); sorted = s.getSortedIndices(); pivot = s.getPivotIndex();
            } else if (state instanceof SearchState s) {
                for (SearchElement e : s.getArray()) { bars.add(new int[]{e.getValue()}); states.add(e.getState()); }
                low = s.getLowIndex(); high = s.getHighIndex(); mid = s.getMidIndex(); current = s.getCurrentIndex(); found = s.getFoundIndex();
            } else return;
            int n = bars.size();
            if (n == 0) return;
            int min = 0, max = 100;
            for (int[] b : bars) { min = Math.min(min, b[0]); max = Math.max(max, b[0]); }
            double slot = Math.min(56, (w - 40) / n), bw = Math.max(30, slot - 10), gap = slot;
            double startX = (w - gap * n) / 2 + (gap - bw) / 2, base = h - 40;
            for (int i = 0; i < n; i++) {
                int v = bars.get(i)[0];
                double bh = Math.max(54, Math.round((v - min) / (double) (max - min) * (h - 150) + 54));
                Color fill = Draw.CREAM; String badge = null; String st = states.get(i);
                if (found != null && i == found || "found".equals(st)) { fill = Draw.SAGE; badge = "FOUND"; }
                else if (pivot != null && i == pivot || "pivot".equals(st)) { fill = Draw.TEAL; badge = "PIVOT"; }
                else if (mid != null && i == mid || "mid".equals(st)) { fill = Draw.TERRA; badge = "MID"; }
                else if (swapping.contains(i) || "swapping".equals(st)) { fill = Draw.TERRA; badge = "SWAP"; }
                else if (comparing.contains(i) || "comparing".equals(st) || (current != null && i == current)) { fill = Draw.MUSTARD; badge = "COMPARE"; }
                else if (sorted.contains(i) || "sorted".equals(st)) fill = Draw.SAGE;
                else if ("eliminated".equals(st)) fill = Color.web("#E5E5E5", 0.5);
                double x = startX + i * gap;
                Draw.box(g, x, base - bh, bw, bh, 16, fill, Draw.CREAM_BORDER);
                Draw.text(g, String.valueOf(v), x + bw / 2, base - bh + 14, Draw.mono(13), Draw.INK);
                Draw.text(g, "#" + i, x + bw / 2, base - 10, Draw.sans(10, FontWeight.BOLD), Color.rgb(43, 38, 32, 0.6));
                if (badge != null) Draw.badge(g, badge, x + bw / 2, base - bh - (i % 2 == 1 ? 30 : 14));
                StringBuilder ptr = new StringBuilder();
                if (low != null && i == low) ptr.append("LOW ");
                if (high != null && i == high) ptr.append("HIGH");
                if (ptr.length() > 0) {
                    double pw = ptr.toString().trim().length() * 6.5 + 10;
                    g.setFill(Draw.MUSTARD);
                    g.fillRoundRect(x + bw / 2 - pw / 2, base + 10, pw, 15, 5, 5);
                    g.setStroke(Draw.INK);
                    g.setLineWidth(1);
                    g.strokeRoundRect(x + bw / 2 - pw / 2, base + 10, pw, 15, 5, 5);
                    Draw.text(g, ptr.toString().trim(), x + bw / 2, base + 17.5, Draw.sans(9, FontWeight.BOLD), Draw.INK);
                }
            }
        }
    }

    /** The array data structure: a row of indexed cells. */
    public static class Cells implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof ArrayDSState s)) return;
            int n = s.getCells().size();
            double cw = 56, gap = 12, total = n * cw + (n - 1) * gap, x0 = (w - total) / 2, y = h / 2 - 32;
            for (int i = 0; i < n; i++) {
                ArrayCell c = s.getCells().get(i);
                Color fill = "active".equals(c.getState()) || "inserted".equals(c.getState()) ? Draw.SAGE
                        : "deleted".equals(c.getState()) ? Draw.TERRA : "shifted".equals(c.getState()) ? Draw.MUSTARD : Draw.WHITE;
                double x = x0 + i * (cw + gap);
                Draw.box(g, x, y, cw, 64, 16, fill, Draw.CREAM_BORDER);
                Draw.text(g, String.valueOf(c.getValue()), x + cw / 2, y + 22, Draw.mono(14), Draw.INK);
                Draw.text(g, "[" + c.getIndex() + "]", x + cw / 2, y + 48, Draw.sans(10, FontWeight.BOLD), Draw.MUTED);
            }
        }
    }

    /** Heap shown twice: as its backing array and as the complete binary tree it represents. */
    public static class Heap implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof HeapDSState s)) return;
            int n = s.getArray().size();
            Draw.textLeft(g, s.getType().toUpperCase() + " HEAP  ·  tree view", 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            int levels = n == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(n);
            double treeH = h - 110;
            for (int i = 0; i < n; i++) {
                int level = 31 - Integer.numberOfLeadingZeros(i + 1);
                int posInLevel = i + 1 - (1 << level);
                double x = w * (posInLevel + 0.5) / (1 << level), y = 36 + level * (treeH / Math.max(levels, 1)) * 0.9;
                if (i > 0) {
                    int p = (i - 1) / 2, pl = 31 - Integer.numberOfLeadingZeros(p + 1), pp = p + 1 - (1 << pl);
                    g.setStroke(Draw.CREAM_BORDER);
                    g.setLineWidth(2);
                    g.strokeLine(w * (pp + 0.5) / (1 << pl), 36 + pl * (treeH / Math.max(levels, 1)) * 0.9, x, y);
                }
            }
            for (int i = 0; i < n; i++) {
                int level = 31 - Integer.numberOfLeadingZeros(i + 1);
                int posInLevel = i + 1 - (1 << level);
                double x = w * (posInLevel + 0.5) / (1 << level), y = 36 + level * (treeH / Math.max(levels, 1)) * 0.9;
                Color fill = s.getComparingIndices().contains(i) ? Draw.MUSTARD : s.getActiveIndices().contains(i) ? Draw.SAGE : Draw.CREAM;
                if ("violation".equals(s.getArray().get(i).getState())) fill = Draw.TERRA;
                g.setFill(fill);
                g.fillOval(x - 18, y - 18, 36, 36);
                g.setStroke(Draw.INK);
                g.setLineWidth(2);
                g.strokeOval(x - 18, y - 18, 36, 36);
                Draw.text(g, String.valueOf(s.getArray().get(i).getValue()), x, y, Draw.mono(12), Draw.INK);
            }
            double cw = 44, gap = 6, x0 = (w - n * (cw + gap)) / 2, y = h - 60;
            Draw.textLeft(g, "array view", 12, y - 16, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            for (int i = 0; i < n; i++) {
                Color fill = s.getComparingIndices().contains(i) ? Draw.MUSTARD : s.getActiveIndices().contains(i) ? Draw.SAGE : Draw.CREAM;
                Draw.box(g, x0 + i * (cw + gap), y, cw, 36, 10, fill, Draw.CREAM_BORDER);
                Draw.text(g, String.valueOf(s.getArray().get(i).getValue()), x0 + i * (cw + gap) + cw / 2, y + 14, Draw.mono(12), Draw.INK);
                Draw.text(g, String.valueOf(i), x0 + i * (cw + gap) + cw / 2, y + 48, Draw.sans(9, FontWeight.BOLD), Draw.MUTED);
            }
        }
    }
}
