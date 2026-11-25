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

        Label logo = label("α", "heading");
        logo.setStyle("-fx-background-color:#DFA43C;-fx-background-radius:20;-fx-min-width:32;-fx-min-height:32;-fx-alignment:center;-fx-font-size:15px;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:20;");
        javafx.scene.text.Text algo = new javafx.scene.text.Text("Algo"), j = new javafx.scene.text.Text("J");
        for (var tx : new javafx.scene.text.Text[]{algo, j}) tx.setStyle("-fx-font-family:'Poppins';-fx-font-weight:900;-fx-font-size:18px;-fx-fill:" + (tx == j ? "#C25A35" : "#2B2620") + ";");
        javafx.scene.text.TextFlow brand = new javafx.scene.text.TextFlow(algo, j);
        String[][] items = {{"Dashboard", "◔"}, {"Algorithms", "▤"}, {"Visualizer", "◎"}, {"Compare", "⇄"}, {"Practice", "✓"}};
        for (String[] it : items) {
            Button b = button(it[1] + "  " + it[0], "tab", () -> show(it[0]));
            tabButtons.put(it[0], b);
            tabs.getChildren().add(b);
        }
        HBox nav = row(10, logo, brand, grow(), tabs, grow(), points);
        nav.getStyleClass().add("navbar");
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setMaxWidth(1216);
        VBox navHolder = new VBox(nav);
        navHolder.setAlignment(Pos.TOP_CENTER);
        navHolder.setPadding(new Insets(14, 15, 0, 0));

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setTop(navHolder);
        root.setCenter(scroll);
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
        tabButtons.forEach((k, b) -> { b.getStyleClass().remove("tab-active"); if (!b.getStyleClass().contains("tab")) b.getStyleClass().add("tab"); if (k.equals(name)) b.getStyleClass().add("tab-active"); });
        Node content = switch (name) {
            case "Dashboard" -> new DashboardView(services, c -> { show("Algorithms"); library.setCategory(c); },
                    () -> openAlgorithm("bubble-sort"), () -> show("Compare"), () -> show("Practice"));
            case "Algorithms" -> library;
            case "Visualizer" -> visualizer;
            case "Compare" -> new CompareView(services);
            default -> new PracticeView(services, this::updatePoints);
        };
        VBox column = new VBox(content);
        column.setMaxWidth(1216);
        column.setPrefWidth(1216);
        VBox page = new VBox(column, footer());
        page.setAlignment(Pos.TOP_CENTER);
        page.setFillWidth(true);
        VBox.setVgrow(column, Priority.ALWAYS);
        column.setMaxWidth(1216);
        scroll.setContent(page);
        scroll.setVvalue(0);
        updatePoints();
    }

    private void updatePoints() {
        points.setText(services.quizzes().correctQuestionCount() * 10 + " pts");
    }

    private Node footer() {
        Label mark = label("α", "heading");
        mark.setStyle("-fx-background-color:#9CCBA2;-fx-background-radius:20;-fx-min-width:32;-fx-min-height:32;-fx-alignment:center;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:20;");
        Label sub = label("Interactive Warm Geometric Data Structures & Algorithms Suite", "muted");
        sub.setStyle("-fx-font-size:11px;-fx-font-weight:700;");
        Label links = label("Dashboard   •   Library   •   Visualizer   •   Comparison   •   Quiz Practice", "heading");
        links.setStyle("-fx-font-size:11px;-fx-font-weight:700;");
        Label mono = label("Java Engine • JavaFX • SQLite via JDBC", "mono");
        mono.setStyle("-fx-font-size:11px;-fx-text-fill:#6B6257;");
        HBox f = row(14, mark, new VBox(0, label("AlgoJ Visualizer", "heading"), sub), grow(), links, grow(), mono);
        f.getStyleClass().add("footer");
        f.setPadding(new Insets(28, 112, 28, 112));
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
