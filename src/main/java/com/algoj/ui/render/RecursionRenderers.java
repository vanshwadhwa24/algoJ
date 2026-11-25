package com.algoj.ui.render;

import com.algoj.model.RecursionAndDPModel.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.*;

/** Call stacks (factorial, naive Fibonacci), Fibonacci tables, Tower of Hanoi pegs, N-Queens board. */
public final class RecursionRenderers {
    private RecursionRenderers() {}

    public static class Factorial implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof FactorialState s)) return;
            Draw.textLeft(g, "Call stack (top at the bottom)", 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            double fw = 220, fh = 34, x = w / 2 - fw / 2;
            for (int i = 0; i < s.getCallStack().size(); i++) {
                StackFrame f = s.getCallStack().get(i);
                double y = 34 + i * (fh + 6);
                Draw.box(g, x, y, fw, fh, 10, "returned".equals(f.getState()) ? Draw.SAGE : "active".equals(f.getState()) ? Draw.MUSTARD : Draw.WHITE, Draw.INK);
                Draw.text(g, f.getName() + (f.getReturnValue() != null ? "  →  " + f.getReturnValue() : ""), w / 2, y + fh / 2, Draw.mono(13), Draw.INK);
            }
            if (s.getResult() != null) Draw.text(g, "Result: " + s.getResult(), w / 2, h - 18, Draw.mono(15), Draw.INK);
        }
    }

    public static class Fibonacci implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof FibonacciState s)) return;
            Draw.textLeft(g, s.getMode() + "  ·  calls " + s.getTotalCalls(), 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            double y = 36;
            for (String frame : s.getCallStack()) {
                Draw.box(g, w / 2 - 110, y, 220, 28, 8, Draw.WHITE, Draw.INK);
                Draw.text(g, frame, w / 2, y + 14, Draw.mono(12), Draw.INK);
                y += 32;
                if (y > h - 90) break;
            }
            if (s.getDpArray() != null && !s.getDpArray().isEmpty()) cells(g, w, h - 70, "dp[]", s.getDpArray(), s.getCurrentIndex());
            if (s.getMemoTable() != null && !s.getMemoTable().isEmpty()) {
                List<Integer> v = new ArrayList<>(s.getMemoTable().values());
                cells(g, w, h - 20, "memo", v, null);
            }
            if (s.getResult() != null) Draw.text(g, "fib(" + s.getN() + ") = " + s.getResult(), w / 2, h - 110, Draw.mono(15), Draw.INK);
        }

        private void cells(GraphicsContext g, double w, double y, String label, List<Integer> v, Integer hot) {
            double cw = 44, gap = 6, x0 = (w - v.size() * (cw + gap)) / 2;
            Draw.textLeft(g, label, 12, y + 12, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            for (int i = 0; i < v.size(); i++) {
                Draw.box(g, x0 + i * (cw + gap), y, cw, 30, 8, hot != null && hot == i ? Draw.SAGE : Draw.CREAM, Draw.CREAM_BORDER);
                Draw.text(g, String.valueOf(v.get(i)), x0 + i * (cw + gap) + cw / 2, y + 15, Draw.mono(12), Draw.INK);
            }
        }
    }

    public static class Hanoi implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof HanoiState s)) return;
            double pegW = w / 3, base = h - 30, dh = 24;
            Color[] cols = {Draw.TERRA, Draw.MUSTARD, Draw.SAGE, Draw.TEAL, Draw.CREAM};
            int i = 0;
            for (String peg : List.of("A", "B", "C")) {
                double cx = pegW * (i + 0.5);
                g.setFill(Draw.INK);
                g.fillRoundRect(cx - 4, base - 180, 8, 180, 4, 4);
                g.fillRoundRect(cx - pegW / 2 + 20, base, pegW - 40, 6, 3, 3);
                Draw.text(g, peg, cx, base + 20, Draw.sans(12, FontWeight.BOLD), Draw.MUTED);
                List<Integer> disks = s.getPegs().getOrDefault(peg, List.of());
                for (int d = 0; d < disks.size(); d++) {
                    int size = disks.get(d);
                    double dw = 40 + size * 26;
                    Draw.box(g, cx - dw / 2, base - (d + 1) * (dh + 2), dw, dh, 10, cols[(size - 1) % cols.length], Draw.INK);
                    Draw.text(g, String.valueOf(size), cx, base - (d + 1) * (dh + 2) + dh / 2, Draw.mono(11), Draw.INK);
                }
                i++;
            }
            Draw.textLeft(g, "moves: " + s.getMoveCount(), 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
        }
    }

    public static class Queens implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof NQueensState s)) return;
            int n = s.getN();
            double cell = Math.min(56, (h - 50) / n), x0 = (w - n * cell) / 2, y0 = 30;
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++) {
                    boolean cur = r == s.getCurrentRow() && c == s.getCurrentCol();
                    g.setFill(cur ? (s.isConflict() ? Draw.TERRA : Draw.MUSTARD) : ((r + c) % 2 == 0 ? Draw.CREAM : Draw.CREAM_BORDER));
                    g.fillRect(x0 + c * cell, y0 + r * cell, cell, cell);
                    if (r < s.getBoard().size() && s.getBoard().get(r) == c) Draw.text(g, "♛", x0 + c * cell + cell / 2, y0 + r * cell + cell / 2, Draw.sans(cell * 0.6, FontWeight.BOLD), Draw.INK);
                }
            g.setStroke(Draw.INK);
            g.setLineWidth(2);
            g.strokeRect(x0, y0, n * cell, n * cell);
            Draw.textLeft(g, "solutions found: " + s.getSolutionsFound(), 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
        }
    }
}
