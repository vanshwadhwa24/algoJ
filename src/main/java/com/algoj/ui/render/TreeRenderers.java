package com.algoj.ui.render;

import com.algoj.model.TreeStructureModel.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.*;

/** Binary search tree, AVL tree (with balance factors) and trie drawings. Layout is computed here from tree shape. */
public final class TreeRenderers {
    private TreeRenderers() {}

    /** A node reduced to what drawing needs, so BST and AVL share one layout. */
    private record N(String label, String sub, String highlight, N left, N right) {}

    private static N fromBst(BSTNode n) {
        return n == null ? null : new N(String.valueOf(n.getValue()), null, n.getHighlight(), fromBst(n.getLeft()), fromBst(n.getRight()));
    }

    private static N fromAvl(AVLNode n) {
        return n == null ? null : new N(String.valueOf(n.getValue()), "h" + n.getHeight() + " bf" + n.getBalanceFactor(), n.getHighlight(),
                fromAvl(n.getLeft()), fromAvl(n.getRight()));
    }

    private static int depth(N n) {
        return n == null ? 0 : 1 + Math.max(depth(n.left()), depth(n.right()));
    }

    private static void layout(N n, int level, int[] counter, Map<N, double[]> pos) {
        if (n == null) return;
        layout(n.left(), level + 1, counter, pos);
        pos.put(n, new double[]{counter[0]++, level});
        layout(n.right(), level + 1, counter, pos);
    }

    private static void drawTree(GraphicsContext g, double w, double h, N root, String footer) {
        if (root == null) {
            Draw.text(g, "Empty tree", w / 2, h / 2, Draw.sans(13, FontWeight.BOLD), Draw.MUTED);
            return;
        }
        Map<N, double[]> pos = new IdentityHashMap<>();
        layout(root, 0, new int[]{0}, pos);
        int count = pos.size(), levels = depth(root);
        double dx = Math.min(64, (w - 60) / Math.max(count, 1)), dy = Math.min(72, (h - 80) / Math.max(levels, 1));
        double x0 = (w - dx * (count - 1)) / 2, y0 = 40;
        g.setStroke(Draw.CREAM_BORDER);
        g.setLineWidth(2.5);
        for (var e : pos.entrySet()) {
            for (N c : new N[]{e.getKey().left(), e.getKey().right()}) {
                if (c != null) {
                    double[] p = e.getValue(), q = pos.get(c);
                    g.strokeLine(x0 + p[0] * dx, y0 + p[1] * dy, x0 + q[0] * dx, y0 + q[1] * dy);
                }
            }
        }
        for (var e : pos.entrySet()) {
            double x = x0 + e.getValue()[0] * dx, y = y0 + e.getValue()[1] * dy;
            g.setFill(Draw.stateColor(e.getKey().highlight(), Draw.WHITE));
            g.fillOval(x - 20, y - 20, 40, 40);
            g.setStroke(Draw.INK);
            g.setLineWidth(2);
            g.strokeOval(x - 20, y - 20, 40, 40);
            Draw.text(g, e.getKey().label(), x, y, Draw.mono(13), Draw.INK);
            if (e.getKey().sub() != null) Draw.text(g, e.getKey().sub(), x, y + 30, Draw.mono(9), Draw.MUTED);
        }
        if (footer != null) Draw.textLeft(g, footer, 12, h - 12, Draw.mono(12), Draw.INK);
    }

    public static class Bst implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof BSTDSState s)) return;
            String out = s.getTraversalOutput() == null || s.getTraversalOutput().isEmpty() ? null : "Traversal: " + s.getTraversalOutput();
            drawTree(g, w, h - 10, fromBst(s.getRoot()), out);
            Draw.textLeft(g, "nodes " + s.getNodeCount() + "  height " + s.getHeight() + "  leaves " + s.getLeafCount(), 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
        }
    }

    public static class Avl implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof AVLDSState s)) return;
            drawTree(g, w, h, fromAvl(s.getRoot()), null);
            Draw.textLeft(g, s.isBalanced() ? "Balanced" : "Rebalancing…", 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
        }
    }

    public static class Trie implements StateRenderer {
        @Override
        public void draw(GraphicsContext g, double w, double h, Object state) {
            if (!(state instanceof TrieDSState s) || s.getRoot() == null) return;
            Map<TrieNode, double[]> pos = new IdentityHashMap<>();
            int[] leaf = {0};
            place(s.getRoot(), 0, leaf, pos);
            int levels = 0;
            for (double[] p : pos.values()) levels = Math.max(levels, (int) p[1] + 1);
            double dx = Math.min(56, (w - 60) / Math.max(leaf[0], 1)), dy = Math.min(64, (h - 90) / Math.max(levels, 1));
            double x0 = (w - dx * (leaf[0] - 1)) / 2, y0 = 36;
            g.setStroke(Draw.CREAM_BORDER);
            g.setLineWidth(2.5);
            for (var e : pos.entrySet())
                for (TrieNode c : e.getKey().getChildren().values()) {
                    double[] p = e.getValue(), q = pos.get(c);
                    g.strokeLine(x0 + p[0] * dx, y0 + p[1] * dy, x0 + q[0] * dx, y0 + q[1] * dy);
                }
            for (var e : pos.entrySet()) {
                double x = x0 + e.getValue()[0] * dx, y = y0 + e.getValue()[1] * dy;
                TrieNode node = e.getKey();
                Color fill = Draw.stateColor(node.getHighlight(), node.isEndOfWord() ? Draw.CREAM : Draw.WHITE);
                g.setFill(fill);
                g.fillOval(x - 17, y - 17, 34, 34);
                g.setStroke(Draw.INK);
                g.setLineWidth(node.isEndOfWord() ? 4 : 2);
                g.strokeOval(x - 17, y - 17, 34, 34);
                Draw.text(g, node.getChar() == null || node.getChar().isEmpty() ? "·" : node.getChar(), x, y, Draw.mono(13), Draw.INK);
            }
            Draw.textLeft(g, "Words: " + s.getWordsList(), 12, h - 14, Draw.mono(12), Draw.INK);
            Draw.textLeft(g, "double ring = end of word", 12, 14, Draw.sans(11, FontWeight.BOLD), Draw.MUTED);
        }

        private void place(TrieNode n, int level, int[] leaf, Map<TrieNode, double[]> pos) {
            List<TrieNode> kids = new ArrayList<>(new TreeMap<>(n.getChildren()).values());
            if (kids.isEmpty()) {
                pos.put(n, new double[]{leaf[0]++, level});
                return;
            }
            double sum = 0;
            for (TrieNode c : kids) {
                place(c, level + 1, leaf, pos);
                sum += pos.get(c)[0];
            }
            pos.put(n, new double[]{sum / kids.size(), level});
        }
    }
}
