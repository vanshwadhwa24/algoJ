package com.algoj.ui.render;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/** Palette and small drawing helpers shared by every renderer (warm geometric theme). */
public final class Draw {
    public static final Color INK = Color.web("#2B2620");
    public static final Color CREAM = Color.web("#FBF3E3");
    public static final Color CREAM_BORDER = Color.web("#E3D3B4");
    public static final Color PAGE = Color.web("#F7EFE1");
    public static final Color MUSTARD = Color.web("#DFA43C");
    public static final Color SAGE = Color.web("#9CCBA2");
    public static final Color TEAL = Color.web("#8FB5AD");
    public static final Color TERRA = Color.web("#D97B54");
    public static final Color WHITE = Color.WHITE;
    public static final Color MUTED = Color.web("#6B6257");

    private Draw() {}

    public static Font mono(double size) {
        return Font.font("JetBrains Mono", FontWeight.BOLD, size);
    }

    public static Font sans(double size, FontWeight w) {
        return Font.font("Poppins", w, size);
    }

    public static void box(GraphicsContext g, double x, double y, double w, double h, double r, Color fill, Color stroke) {
        g.setFill(Color.rgb(43, 38, 32, 0.14));
        g.fillRoundRect(x, y + 3, w, h, r, r);
        g.setFill(fill);
        g.fillRoundRect(x, y, w, h, r, r);
        g.setStroke(stroke);
        g.setLineWidth(2);
        g.strokeRoundRect(x, y, w, h, r, r);
    }

    public static void text(GraphicsContext g, String s, double x, double y, Font f, Color c) {
        g.setFont(f);
        g.setFill(c);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(s, x, y);
    }

    public static void textLeft(GraphicsContext g, String s, double x, double y, Font f, Color c) {
        g.setFont(f);
        g.setFill(c);
        g.setTextAlign(TextAlignment.LEFT);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(s, x, y);
    }

    public static void badge(GraphicsContext g, String s, double cx, double cy) {
        double w = s.length() * 6.2 + 12;
        g.setFill(INK);
        g.fillRoundRect(cx - w / 2, cy - 8, w, 16, 6, 6);
        text(g, s, cx, cy, sans(9, FontWeight.BOLD), WHITE);
    }

    public static Color stateColor(String state, Color fallback) {
        if (state == null) return fallback;
        return switch (state) {
            case "active", "inserted", "found", "new", "top", "sorted", "visited", "mst" -> SAGE;
            case "comparing", "current", "compare", "queued", "probed", "enqueueing", "pushing", "relaxed" -> MUSTARD;
            case "deleted", "swapping", "mid", "dequeueing", "popping", "collision", "rejected", "imbalanced", "violation" -> TERRA;
            case "pivot", "rotating", "front", "rear", "occupied", "shifted", "placed" -> TEAL;
            default -> fallback;
        };
    }
}
