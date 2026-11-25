package com.algoj.ui;

import com.algoj.dto.ExecutionStep;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;

import java.util.List;

/** Plays a recorded trace forwards, backwards or by scrubbing. It holds no drawing code. */
public class Playback {

    private List<ExecutionStep> steps = List.of();
    private final IntegerProperty index = new SimpleIntegerProperty(0);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1.0);
    private final Timeline timer = new Timeline();

    public Playback() {
        speed.addListener((o, a, b) -> restartTimerIfPlaying());
    }

    public void load(List<ExecutionStep> trace) {
        pause();
        steps = trace == null ? List.of() : List.copyOf(trace);
        index.set(0);
    }

    public int total() {
        return steps.size();
    }

    public ExecutionStep current() {
        return steps.isEmpty() ? null : steps.get(index.get());
    }

    public boolean atEnd() {
        return steps.isEmpty() || index.get() >= steps.size() - 1;
    }

    public void goTo(int i) {
        if (steps.isEmpty()) return;
        index.set(Math.max(0, Math.min(steps.size() - 1, i)));
        if (atEnd()) pause();
    }

    public void forward() {
        goTo(index.get() + 1);
    }

    public void back() {
        goTo(index.get() - 1);
    }

    public void reset() {
        pause();
        goTo(0);
    }

    public void play() {
        if (steps.isEmpty()) return;
        if (atEnd()) index.set(0);
        playing.set(true);
        restartTimerIfPlaying();
    }

    public void pause() {
        playing.set(false);
        timer.stop();
    }

    private void restartTimerIfPlaying() {
        timer.stop();
        if (!playing.get()) return;
        timer.getKeyFrames().setAll(new KeyFrame(Duration.millis(700 / speed.get()), e -> forward()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public DoubleProperty speedProperty() {
        return speed;
    }
}
