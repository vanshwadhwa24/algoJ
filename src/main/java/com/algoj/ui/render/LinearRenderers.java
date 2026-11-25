package com.algoj.ui.render;

import com.algoj.model.LinearStructureModel.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/** Linked list, stack, queue and hash table drawings. */
public final class LinearRenderers {
    private LinearRenderers() {}

    public static class LinkedList implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof LinkedListDSState s)) return;
            int n = s.getNodes().size();
            double nw = 72, gap = 40, total = n * nw + Math.max(0, n - 1) * gap, x0 = Math.max(20, (w - total) / 2), y = h / 2 - 24;
            Draw.textLeft(g, "HEAD", x0, y - 18, Draw.sans(10, FontWeight.BOLD), Draw.MUTED);
            for (int i = 0; i < n; i++) {
                ListNode node = s.getNodes().get(i);
                double x = x0 + i * (nw + gap);
                Color fill = node.getId().equals(s.getActiveNodeId()) ? Draw.MUSTARD : Draw.stateColor(node.getHighlight(), Draw.WHITE);
                Draw.box(g, x, y, nw, 48, 14, fill, Draw.INK);
                Draw.text(g, String.valueOf(node.getValue()), x + nw * 0.38, y + 24, Draw.mono(14), Draw.INK);
                g.setStroke(Draw.INK);
                g.setLineWidth(1.5);
                g.strokeLine(x + nw * 0.7, y, x + nw * 0.7, y + 48);
                if (i < n - 1) {
                    double ax = x + nw, ay = y + 24, bx = x + nw + gap;
                    g.strokeLine(ax - 8, ay, bx - 4, ay);
                    g.strokeLine(bx - 4, ay, bx - 10, ay - 4);
                    g.strokeLine(bx - 4, ay, bx - 10, ay + 4);
                    if (s.isDoubly()) g.strokeLine(bx - 4, ay + 12, ax + 4, ay + 12);
                } else {
                    Draw.text(g, "NULL", x + nw + 28, y + 24, Draw.mono(11), Draw.MUTED);
                }
            }
            if (n == 0) Draw.text(g, "Empty list (head = NULL)", w / 2, h / 2, Draw.sans(13, FontWeight.BOLD), Draw.MUTED);
        }
    }

    public static class Stack implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof StackDSState s)) return;
            double iw = 160, ih = 34, gap = 6, cx = w / 2, bottom = h - 20;
            int cap = s.getMaxCapacity();
            double frameH = cap * (ih + gap) + 8;
            g.setStroke(Draw.INK);
            g.setLineWidth(3);
            double fx = cx - iw / 2 - 8, fy = bottom - frameH;
            g.strokeLine(fx, fy, fx, bottom);
            g.strokeLine(fx + iw + 16, fy, fx + iw + 16, bottom);
            g.strokeLine(fx, bottom, fx + iw + 16, bottom);
            for (int i = 0; i < s.getItems().size(); i++) {
                StackItem it = s.getItems().get(i);
                double y = bottom - (i + 1) * (ih + gap);
                Draw.box(g, cx - iw / 2, y, iw, ih, 10, Draw.stateColor(it.getHighlight(), Draw.WHITE), Draw.INK);
                Draw.text(g, String.valueOf(it.getValue()), cx, y + ih / 2, Draw.mono(14), Draw.INK);
                if (i == s.getItems().size() - 1) Draw.textLeft(g, "TOP ◀", cx + iw / 2 + 24, y + ih / 2, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            }
            Draw.textLeft(g, "capacity " + s.getItems().size() + "/" + cap, 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
        }
    }

    public static class Queue implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof QueueDSState s)) return;
            int n = s.getSlots().size();
            double sw = 64, gap = 10, x0 = (w - (n * sw + (n - 1) * gap)) / 2, y = h / 2 - 28;
            Draw.textLeft(g, (s.isCircular() ? "Circular queue" : "Linear queue") + "  size " + s.getSize() + "/" + s.getCapacity(), 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            for (int i = 0; i < n; i++) {
                QueueSlot slot = s.getSlots().get(i);
                double x = x0 + i * (sw + gap);
                Color fill = slot.getValue() == null ? Draw.WHITE : Draw.stateColor(slot.getState(), Draw.CREAM);
                Draw.box(g, x, y, sw, 56, 14, fill, Draw.INK);
                Draw.text(g, slot.getValue() == null ? "" : String.valueOf(slot.getValue()), x + sw / 2, y + 28, Draw.mono(14), Draw.INK);
                Draw.text(g, "[" + i + "]", x + sw / 2, y + 74, Draw.sans(10, FontWeight.BOLD), Draw.MUTED);
                if (s.getSize() > 0 && i == s.getFront()) Draw.badge(g, "FRONT", x + sw / 2, y - 14);
                if (s.getSize() > 0 && i == s.getRear()) Draw.badge(g, "REAR", x + sw / 2, y + 96);
            }
        }
    }

    public static class HashTable implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof HashTableDSState s)) return;
            int n = s.getBuckets().size();
            double rowH = Math.min(44, (h - 40) / Math.max(n, 1)), x0 = Math.max(20, w / 2 - 260);
            Draw.textLeft(g, s.getCollisionStrategy().equals("probing") ? "Linear probing" : "Separate chaining", 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
            for (int i = 0; i < n; i++) {
                HashBucket b = s.getBuckets().get(i);
                double y = 30 + i * rowH;
                boolean hot = s.getActiveHash() != null && s.getActiveHash() == i;
                Draw.box(g, x0, y, 52, rowH - 6, 10, hot ? Draw.MUSTARD : Draw.stateColor(b.getState(), Draw.CREAM), Draw.INK);
                Draw.text(g, String.valueOf(i), x0 + 26, y + (rowH - 6) / 2, Draw.mono(13), Draw.INK);
                double ex = x0 + 70;
                for (HashEntry e : b.getEntries()) {
                    boolean deleted = Boolean.TRUE.equals(e.getIsDeleted());
                    Draw.box(g, ex, y, 130, rowH - 6, 10, deleted ? Draw.TERRA : Draw.WHITE, Draw.INK);
                    Draw.text(g, (deleted ? "✗ " : "") + e.getKey() + " : " + e.getValue(), ex + 65, y + (rowH - 6) / 2, Draw.mono(11), Draw.INK);
                    g.setStroke(Draw.INK);
                    g.strokeLine(ex + 130, y + (rowH - 6) / 2, ex + 148, y + (rowH - 6) / 2);
                    ex += 148;
                }
            }
        }
    }
}
