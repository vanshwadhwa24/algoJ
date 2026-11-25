package com.algoj.ui.render;

import com.algoj.model.GraphModel.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.Map;

/** Draws any graph algorithm state: vertices, weighted edges, and the live distance table when present. */
public class GraphRenderer implements StateRenderer {
    @Override
    public void draw(GraphicsContext g, double w, double h, Object state) {
        GraphData graph; Map<String, Object> dist = null; String info = null; java.util.List<String> order = null;
        if (state instanceof GraphTraversalState s) { graph = s.getGraph(); order = s.getTraversalOrder(); info = "Queue/Stack: " + s.getQueueOrStack(); }
        else if (state instanceof ShortestPathState s) { graph = s.getGraph(); dist = s.getDistances(); }
        else if (state instanceof MSTState s) { graph = s.getGraph(); info = "MST weight: " + s.getTotalWeight(); }
        else return;
        double scale = Math.min((w - 80) / 560.0, (h - 120) / 220.0), ox = (w - 560 * scale) / 2 - 60 * scale, oy = 40;
        for (GraphEdge e : graph.getEdges()) {
            GraphVertex a = vertex(graph, e.getSource()), b = vertex(graph, e.getTarget());
            if (a == null || b == null) continue;
            double x1 = ox + a.getX() * scale, y1 = oy + (a.getY() - 50) * scale, x2 = ox + b.getX() * scale, y2 = oy + (b.getY() - 50) * scale;
            boolean hot = e.getState() != null && !e.getState().equals("default");
            g.setStroke("rejected".equals(e.getState()) ? Draw.TERRA : hot ? Draw.stateColor(e.getState(), Draw.MUSTARD) : Draw.CREAM_BORDER);
            g.setLineWidth(hot ? 5 : 3);
            g.strokeLine(x1, y1, x2, y2);
            if (graph.isDirected() || e.isDirected()) {
                double ang = Math.atan2(y2 - y1, x2 - x1), ax = x2 - 24 * Math.cos(ang), ay = y2 - 24 * Math.sin(ang);
                g.setFill(Draw.INK);
                g.fillPolygon(new double[]{ax + 9 * Math.cos(ang), ax + 6 * Math.cos(ang + 2.5), ax + 6 * Math.cos(ang - 2.5)},
                        new double[]{ay + 9 * Math.sin(ang), ay + 6 * Math.sin(ang + 2.5), ay + 6 * Math.sin(ang - 2.5)}, 3);
            }
            if (graph.isWeighted()) {
                double mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
                g.setFill(Draw.WHITE);
                g.fillRoundRect(mx - 12, my - 9, 24, 18, 8, 8);
                Draw.text(g, String.valueOf(e.getWeight()), mx, my, Draw.mono(10), Draw.INK);
            }
        }
        for (GraphVertex v : graph.getVertices()) {
            double x = ox + v.getX() * scale, y = oy + (v.getY() - 50) * scale;
            g.setFill(Draw.stateColor(v.getState(), Draw.WHITE));
            g.fillOval(x - 22, y - 22, 44, 44);
            g.setStroke(Draw.INK);
            g.setLineWidth(2.5);
            g.strokeOval(x - 22, y - 22, 44, 44);
            Draw.text(g, v.getLabel(), x, y, Draw.mono(14), Draw.INK);
            if (dist != null && dist.containsKey(v.getId())) {
                Object d = dist.get(v.getId());
                boolean inf = d == null || (d instanceof Number n && Double.isInfinite(n.doubleValue())) || "Infinity".equals(String.valueOf(d));
                String t = inf ? "∞" : String.valueOf(((Number) d).intValue());
                Draw.text(g, "d=" + t, x, y + 34, Draw.mono(10), Draw.MUTED);
            }
        }
        if (order != null) Draw.textLeft(g, "Order: " + order, 12, h - 28, Draw.mono(12), Draw.INK);
        if (info != null) Draw.textLeft(g, info, 12, h - 10, Draw.mono(12), Draw.MUTED);
    }

    private static GraphVertex vertex(GraphData g, String id) {
        return g.getVertices().stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }
}
