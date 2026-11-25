package com.algoj.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.algoj.ui.Kit.*;

/** JavaFX shell: floating navbar, the five screens, and the footer. */
public class AlgoJApp extends Application {
    private Services services;
    private final BorderPane root = new BorderPane();
    private final ScrollPane scroll = new ScrollPane();
    private final HBox tabs = new HBox(6);
    private final Label points = label("", "points-pill");
    private final Map<String, Button> tabButtons = new LinkedHashMap<>();
    private LibraryView library;
    private VisualizerView visualizer;

    @Override
    public void start(Stage stage) {
        Scene scene = build(Services.open(databaseFile()));
        stage.setTitle("AlgoJ");
        stage.setScene(scene);
        stage.show();
    }

    static Path databaseFile() {
        String p = System.getProperty("algoj.db");
        return p != null ? Path.of(p) : com.algoj.persistence.Database.defaultFile();
    }

    /** Builds the whole UI; separated from start() so headless tests can drive it. */
    public Scene build(Services services) {
        this.services = services;
        loadFonts();
        library = new LibraryView(services, a -> { show("Visualizer"); visualizer.open(a); });
        visualizer = new VisualizerView(services);
        visualizer.open(services.catalog().find("bubble-sort").orElseThrow());

        Label logo = label("α", "badge");
        logo.setStyle("-fx-background-color:#2B2620;-fx-text-fill:white;-fx-background-radius:20;-fx-padding:6 12 6 12;-fx-font-size:16px;");
        Label brand = label("AlgoJ", "heading");
        brand.setStyle("-fx-font-size:20px;");
        for (String name : new String[]{"Dashboard", "Algorithms", "Visualizer", "Compare", "Practice"}) {
            Button b = button(name, "tab", () -> show(name));
            tabButtons.put(name, b);
            tabs.getChildren().add(b);
        }
        HBox nav = row(18, logo, brand, grow(), tabs, points);
        nav.getStyleClass().add("navbar");
        nav.setAlignment(Pos.CENTER_LEFT);
        BorderPane.setMargin(nav, new Insets(18, 40, 8, 40));

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setTop(nav);
        root.setCenter(scroll);
        root.setBottom(footer());
        show("Dashboard");
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(getClass().getResource("/algoj.css").toExternalForm());
        return scene;
    }

    public void openAlgorithm(String id) {
        show("Visualizer");
        visualizer.open(services.catalog().find(id).orElseThrow());
    }

    public void show(String name) {
        tabButtons.forEach((k, b) -> { b.getStyleClass().removeAll("tab", "tab-active"); b.getStyleClass().add(k.equals(name) ? "tab-active" : "tab"); });
        Node content = switch (name) {
            case "Dashboard" -> new DashboardView(services, c -> { show("Algorithms"); library.setCategory(c); }, () -> show("Compare"), () -> show("Practice"));
            case "Algorithms" -> library;
            case "Visualizer" -> visualizer;
            case "Compare" -> new CompareView(services);
            default -> new PracticeView(services, this::updatePoints);
        };
        VBox page = new VBox(content);
        page.setPadding(new Insets(0, 40, 0, 40));
        scroll.setContent(page);
        scroll.setVvalue(0);
        updatePoints();
    }

    private void updatePoints() {
        points.setText(services.quizzes().correctQuestionCount() * 10 + " pts");
    }

    private Node footer() {
        HBox f = row(20, label("AlgoJ Visualizer", "heading"), grow(), label("Java engine  ·  JavaFX  ·  SQLite via JDBC", "muted"));
        f.getStyleClass().add("footer");
        f.setAlignment(Pos.CENTER_LEFT);
        return f;
    }

    private static void loadFonts() {
        for (String f : new String[]{"Poppins-Regular", "Poppins-SemiBold", "Poppins-Bold", "Poppins-ExtraBold"})
            Font.loadFont(AlgoJApp.class.getResourceAsStream("/fonts/" + f + ".ttf"), 12);
        Font.loadFont(AlgoJApp.class.getResourceAsStream("/fonts/JetBrainsMono.ttf"), 12);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
