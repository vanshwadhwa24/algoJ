package com.algoj.ui.render;

import javafx.scene.canvas.GraphicsContext;

/** Draws one execution step's state onto a canvas of the given size. */
public interface StateRenderer {
    void draw(GraphicsContext g, double width, double height, Object state);
}
