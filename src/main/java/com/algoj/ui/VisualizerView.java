package com.algoj.ui;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.catalog.AlgorithmInfo;
import com.algoj.dto.ExecutionStep;
import com.algoj.persistence.SavedInput;
import com.algoj.structure.Operation;
import com.algoj.ui.render.Renderers;
import com.algoj.ui.render.StateRenderer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;

import static com.algoj.ui.Kit.*;

/** The main screen: pick an algorithm or structure, drive it, and step through its recorded trace. */
public class VisualizerView extends VBox {
    private final Services svc;
    private final Playback playback = new Playback();
    private final Canvas canvas = new Canvas(1000, 380);
    private final Label stepLabel = label("", "heading"), percent = label("", "muted"), description = label("", "heading");
    private final Label counters = label("", "badge");
    private final Slider scrub = new Slider(0, 0, 0);
    private final VBox codeLines = new VBox(2);
    private final VBox lower = new VBox(16);
    private final HBox inputs = new HBox(10);
    private final BorderPane body = new BorderPane();
    private final ComboBox<AlgorithmInfo> picker = new ComboBox<>();
    private final HBox badgeHolder = new HBox();
    private final Button playBtn = new Button("▶  Play");
    private boolean updatingScrub;

    private AlgorithmInfo info;
    private Session session;
    private StateRenderer renderer;
    private java.util.function.Supplier<AlgorithmInput> input = () -> AlgorithmInput.empty();
    private String inputText = "";

    public VisualizerView(Services svc) {
        this.svc = svc;
        setSpacing(18);
        setPadding(new Insets(10, 0, 20, 0));

        picker.setItems(FXCollections.observableArrayList(svc.catalog().algorithms()));
        picker.setConverter(new javafx.util.StringConverter<>() {
            public String toString(AlgorithmInfo a) { return a == null ? "" : a.name(); }
            public AlgorithmInfo fromString(String s) { return null; }
        });
        picker.setOnAction(e -> { if (picker.getValue() != null && picker.getValue() != info) open(picker.getValue()); });

        Button canvasTab = button("Interactive Canvas", "tab-active", () -> {});
        Button theoryTab = button("Theory & Notes", "tab", () -> {});
        Button practiceTab = button("Practice", "tab", () -> {});
        List<Button> tabs = List.of(canvasTab, theoryTab, practiceTab);
        Consumer<Integer> showTab = i -> {
            for (int k = 0; k < tabs.size(); k++) { tabs.get(k).getStyleClass().removeAll("tab", "tab-active"); tabs.get(k).getStyleClass().add(k == i ? "tab-active" : "tab"); }
            body.setCenter(i == 0 ? canvasPane() : i == 1 ? theory() : practice());
        };
        canvasTab.setOnAction(e -> showTab.accept(0));
        theoryTab.setOnAction(e -> showTab.accept(1));
        practiceTab.setOnAction(e -> showTab.accept(2));
        this.showCanvas = () -> showTab.accept(0);

        HBox header = row(14, picker, badgeHolder, grow(), canvasTab, theoryTab, practiceTab);
        header.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(header, body);

        scrub.valueProperty().addListener((o, a, b) -> { if (!updatingScrub) playback.goTo(b.intValue()); });
        playback.indexProperty().addListener((o, a, b) -> refresh());
        playback.playingProperty().addListener((o, a, b) -> playBtn.setText(b ? "❚❚  Pause" : "▶  Play"));
        canvas.widthProperty().addListener(o -> draw());
    }

    private Runnable showCanvas;

    public void open(AlgorithmInfo a) {
        info = a;
        if (picker.getValue() != a) picker.setValue(a);
        session = new Session(a);
        renderer = Renderers.forInfo(a);
        badgeHolder.getChildren().setAll(difficultyBadge(a.difficulty()));
        buildInputs();
        buildLower();
        showCanvas.run();
        if (session.isStructure()) playback.load(session.idle()); else run();
        refresh();
    }

    // ---------- tabs ----------

    private Node canvasPane() {
        StackPane holder = new StackPane(canvas);
        holder.setStyle("-fx-background-color:white;-fx-background-radius:20;-fx-border-color:#2B2620;-fx-border-width:2;-fx-border-radius:20;");
        holder.widthProperty().addListener((o, a, w) -> { canvas.setWidth(w.doubleValue() - 4); });
        canvas.setHeight(380);

        scrub.setMinorTickCount(0);
        VBox timeline = card(row(10, stepLabel, grow(), percent), description, scrub);
        HBox controls = playbackControls();
        VBox box = new VBox(16, card(inputs), canvasCard(holder), timeline, controls, lower);
        return box;
    }

    private VBox canvasCard(Node holder) {
        HBox head = row(10, label("Execution Canvas", "heading"), grow(), counters);
        head.setAlignment(Pos.CENTER_LEFT);
        return card(head, holder);
    }

    private HBox playbackControls() {
        Button reset = button("⟲", "light-btn", playback::reset), back = button("◀", "light-btn", playback::back),
                fwd = button("▶", "light-btn", playback::forward);
        playBtn.getStyleClass().add("pill-btn");
        playBtn.setOnAction(e -> { if (playback.playingProperty().get()) playback.pause(); else playback.play(); });
        Button replay = button("↻", "light-btn", () -> { playback.reset(); playback.play(); });
        HBox speeds = new HBox(6);
        ToggleGroup tg = new ToggleGroup();
        for (double s : new double[]{0.5, 1, 1.5, 2, 4}) {
            ToggleButton t = new ToggleButton((s == (int) s ? String.valueOf((int) s) : String.valueOf(s)) + "x");
            t.getStyleClass().add("chip");
            t.setToggleGroup(tg);
            t.setSelected(s == 1);
            t.setOnAction(e -> { if (!t.isSelected()) t.setSelected(true); playback.speedProperty().set(s); updateChips(tg); });
            speeds.getChildren().add(t);
        }
        updateChips(tg);
        HBox c = row(12, reset, back, playBtn, fwd, replay, speeds);
        c.setAlignment(Pos.CENTER);
        return c;
    }

    private void updateChips(ToggleGroup tg) {
        for (Toggle t : tg.getToggles()) {
            var n = ((ToggleButton) t).getStyleClass();
            n.remove("chip-active");
            if (t.isSelected()) n.add("chip-active");
        }
    }

    private Node theory() {
        AlgorithmInfo.Learning l = info.learning();
        VBox v = new VBox(14);
        v.getChildren().add(card(label("Intuition", "heading"), wrap(l == null ? info.description() : l.intuition())));
        if (l != null) {
            v.getChildren().add(card(label("How it works", "heading"), numbered(l.howItWorks())));
            v.getChildren().add(card(label("Edge cases", "heading"), bullets(l.edgeCases())));
            HBox pc = row(16, card(label("Advantages", "heading"), bullets(l.advantages())), card(label("Limitations", "heading"), bullets(l.limitations())));
            pc.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
            v.getChildren().add(pc);
        }
        return v;
    }

    private Node practice() {
        List<com.algoj.catalog.PracticeQuestion> qs = svc.catalog().questionsFor(info.id());
        VBox v = new VBox(14);
        if (qs.isEmpty()) v.getChildren().add(card(wrap("No practice questions for this topic yet.")));
        for (var q : qs) v.getChildren().add(new QuestionCard(q, svc.quizzes(), () -> {}));
        return v;
    }

    private static Label wrap(String s) {
        Label l = new Label(s);
        l.setWrapText(true);
        return l;
    }

    private static VBox bullets(List<String> items) {
        VBox v = new VBox(6);
        if (items != null) items.forEach(s -> v.getChildren().add(wrap("•  " + s)));
        return v;
    }

    private static VBox numbered(List<String> items) {
        VBox v = new VBox(6);
        for (int i = 0; items != null && i < items.size(); i++) v.getChildren().add(wrap((i + 1) + ".  " + items.get(i)));
        return v;
    }

    // ---------- inputs ----------

    private void buildInputs() {
        inputs.getChildren().clear();
        inputs.setAlignment(Pos.CENTER_LEFT);
        inputs.setSpacing(10);
        String id = info.id();
        if (info.isSortOrSearch()) {
            TextField arr = field(String.join(",", ints(info.defaultInput())), 280);
            TextField target = field("", 90);
            target.setPromptText("target");
            Runnable go = () -> {
                inputText = arr.getText() + (target.getText().isBlank() ? "" : " | target=" + target.getText());
                input = () -> AlgorithmInput.ofArray(parseInts(arr.getText()), parseInt(target.getText()));
                run();
            };
            arr.setOnAction(e -> go.run());
            target.setOnAction(e -> go.run());
            Button random = button("Randomize", "light-btn", () -> {
                Random r = new Random();
                List<String> vals = new ArrayList<>();
                for (int i = 0; i < 7; i++) vals.add(String.valueOf(5 + r.nextInt(90)));
                if (id.equals("binary-search")) { vals.sort(Comparator.comparingInt(Integer::parseInt)); }
                arr.setText(String.join(",", vals));
                go.run();
            });
            inputs.getChildren().addAll(label("Array", "muted"), arr);
            if (info.category().equals("searching")) { inputs.getChildren().add(target); target.setText(id.equals("binary-search") ? "23" : "78"); }
            inputs.getChildren().addAll(random, button("Run", "pill-btn", go::run));
            saveLoad(() -> inputText, s -> { String[] p = s.split(" \\| target="); arr.setText(p[0]); target.setText(p.length > 1 ? p[1] : ""); go.run(); });
            go.run();
        } else if (session.isStructure()) {
            structureControls(id);
        } else {
            switch (id) {
                case "factorial" -> slider("n", 1, 7, 4, n -> input = () -> new AlgorithmInput(null, null, n, null, null, null, null, null));
                case "fibonacci" -> {
                    String[] mode = {"tabulated"};
                    int[] n = {6};
                    Runnable go = () -> { input = () -> new AlgorithmInput(null, null, n[0], mode[0], null, null, null, null); run(); };
                    slider("n", 1, 10, 6, v -> { n[0] = v; go.run(); });
                    for (String m : List.of("recursive", "memoized", "tabulated")) {
                        Button b = button(m, m.equals("tabulated") ? "dark-btn" : "light-btn", () -> { mode[0] = m; go.run(); fibStyle(m); });
                        b.setUserData(m);
                        inputs.getChildren().add(b);
                    }
                    input = () -> new AlgorithmInput(null, null, n[0], mode[0], null, null, null, null);
                }
                case "tower-of-hanoi" -> slider("disks", 1, 5, 3, n -> input = () -> new AlgorithmInput(null, null, null, null, n, null, null, null));
                case "n-queens" -> slider("n", 4, 6, 4, n -> input = () -> new AlgorithmInput(null, null, n, null, null, null, null, null));
                case "knapsack" -> slider("capacity", 1, 8, 5, n -> input = () -> new AlgorithmInput(null, null, n, null, null, null, null, null));
                case "lcs" -> {
                    TextField a = field("STONE", 160), b = field("LONGEST", 160);
                    Runnable go = () -> { inputText = a.getText() + " | " + b.getText(); input = () -> new AlgorithmInput(null, null, null, null, null, a.getText().toUpperCase(), b.getText().toUpperCase(), null); run(); };
                    a.setOnAction(e -> go.run());
                    b.setOnAction(e -> go.run());
                    inputs.getChildren().addAll(label("String A", "muted"), a, label("String B", "muted"), b, button("Run", "pill-btn", go::run));
                    saveLoad(() -> inputText, s -> { String[] p = s.split(" \\| "); if (p.length == 2) { a.setText(p[0]); b.setText(p[1]); go.run(); } });
                }
                default -> {
                    com.algoj.model.GraphModel.GraphData[] graph = {com.algoj.algorithm.GraphAlgorithm.sampleGraph()};
                    input = () -> new AlgorithmInput(null, null, null, null, null, null, null, graph[0]);
                    String[] names = {"Standard Graph", "Disconnected Graph", "Negative Weights Graph"};
                    java.util.function.Supplier<com.algoj.model.GraphModel.GraphData>[] makers = new java.util.function.Supplier[]{
                            com.algoj.algorithm.GraphAlgorithm::sampleGraph, com.algoj.algorithm.GraphAlgorithm::disconnectedGraph,
                            com.algoj.algorithm.GraphAlgorithm::negativeWeightsGraph};
                    for (int i = 0; i < names.length; i++) {
                        var maker = makers[i];
                        inputs.getChildren().add(button(names[i], "light-btn", () -> { graph[0] = maker.get(); inputText = maker.get().getVertices().size() + " vertices"; run(); }));
                    }
                }
            }
        }
        if (inputs.getChildren().isEmpty()) inputs.getChildren().add(label("No input needed", "muted"));
    }

    private void fibStyle(String active) {
        for (Node n : inputs.getChildren())
            if (n instanceof Button b && b.getUserData() != null) {
                b.getStyleClass().removeAll("dark-btn", "light-btn");
                b.getStyleClass().add(b.getUserData().equals(active) ? "dark-btn" : "light-btn");
            }
    }

    private void slider(String name, int min, int max, int value, Consumer<Integer> onChange) {
        Slider s = new Slider(min, max, value);
        s.setMajorTickUnit(1);
        s.setMinorTickCount(0);
        s.setSnapToTicks(true);
        s.setPrefWidth(220);
        Label l = label(name + " = " + value, "heading");
        s.valueProperty().addListener((o, a, b) -> {
            if (b.intValue() == a.intValue()) return;
            l.setText(name + " = " + b.intValue());
            inputText = name + "=" + b.intValue();
            onChange.accept(b.intValue());
            if (!"fibonacci".equals(info.id())) run();
        });
        inputs.getChildren().addAll(l, s);
        inputText = name + "=" + value;
        onChange.accept(value);
        saveLoad(() -> inputText, str -> { int v = Integer.parseInt(str.split("=")[1]); s.setValue(v); });
    }

    private void structureControls(String id) {
        TextField value = field("", 80), index = field("", 70), key = field("", 90), text = field("", 110);
        value.setPromptText("value");
        index.setPromptText("index");
        key.setPromptText("key");
        text.setPromptText(id.equals("tries") ? "word" : "value");
        List<String[]> ops = switch (id) {
            case "arrays" -> List.of(op("access", "op-gold"), op("update", "op-teal"), op("insert", "op-sage"), op("delete", "op-terra"), op("search", "op-gold"), op("traverse", "op-teal"));
            case "linked-lists" -> List.of(op("insertHead", "op-sage"), op("insertTail", "op-sage"), op("insertAt", "op-sage"), op("deleteHead", "op-terra"), op("deleteTail", "op-terra"), op("deleteAt", "op-terra"), op("reverse", "op-teal"), op("search", "op-gold"), op("traverse", "op-teal"));
            case "stacks" -> List.of(op("push", "op-sage"), op("pop", "op-terra"), op("peek", "op-gold"), op("isEmpty", "op-teal"));
            case "queues" -> List.of(op("enqueue", "op-sage"), op("dequeue", "op-terra"), op("peekFront", "op-gold"), op("isEmpty", "op-teal"));
            case "hash-tables" -> List.of(op("insert", "op-sage"), op("delete", "op-terra"), op("search", "op-gold"));
            case "binary-search-tree" -> List.of(op("insert", "op-sage"), op("delete", "op-terra"), op("search", "op-gold"), op("findMin", "op-gold"), op("findMax", "op-gold"), op("inorder", "op-teal"), op("preorder", "op-teal"), op("postorder", "op-teal"), op("levelorder", "op-teal"));
            case "avl-trees" -> List.of(op("insert", "op-sage"), op("search", "op-gold"));
            case "binary-heaps" -> List.of(op("insert", "op-sage"), op("extract", "op-terra"), op("peek", "op-gold"));
            default -> List.of(op("insert", "op-sage"), op("delete", "op-terra"), op("search", "op-gold"));
        };
        boolean hash = id.equals("hash-tables"), trie = id.equals("tries"), idx = id.equals("arrays") || id.equals("linked-lists");
        if (hash) inputs.getChildren().addAll(key, text);
        else if (trie) inputs.getChildren().add(text);
        else inputs.getChildren().add(value);
        if (idx) inputs.getChildren().add(index);
        for (String[] o : ops) {
            Button b = new Button(o[0]);
            b.getStyleClass().addAll("op-btn", o[1]);
            b.setOnAction(e -> {
                Operation operation = new Operation(o[0], parseInt(value.getText()), parseInt(index.getText()), key.getText(), text.getText());
                List<ExecutionStep> steps = session.apply(operation);
                inputText = o[0] + " " + (hash ? key.getText() + ":" + text.getText() : trie ? text.getText() : value.getText());
                svc.runs().record(info.id(), inputText, steps.size());
                playback.load(steps);
                playback.goTo(0);
                playback.play();
            });
            inputs.getChildren().add(b);
        }
        inputs.getChildren().add(button("Reset", "light-btn", () -> playback.load(session.reset())));
        FlowPane.setMargin(inputs, Insets.EMPTY);
    }

    private static String[] op(String n, String style) {
        return new String[]{n, style};
    }

    private void saveLoad(java.util.function.Supplier<String> current, Consumer<String> apply) {
        ComboBox<SavedInput> saved = new ComboBox<>();
        saved.setPromptText("Saved inputs");
        saved.setConverter(new javafx.util.StringConverter<>() {
            public String toString(SavedInput s) { return s == null ? "" : s.name(); }
            public SavedInput fromString(String s) { return null; }
        });
        Runnable reload = () -> saved.setItems(FXCollections.observableArrayList(svc.savedInputs().forAlgorithm(info.id())));
        reload.run();
        saved.setOnAction(e -> { if (saved.getValue() != null) apply.accept(saved.getValue().payload()); });
        Button save = button("Save input", "light-btn", () -> {
            String p = current.get();
            if (p == null || p.isBlank()) return;
            svc.savedInputs().save(info.id(), p, p);
            reload.run();
        });
        inputs.getChildren().addAll(grow(), save, saved);
    }

    private static TextField field(String v, double w) {
        TextField f = new TextField(v);
        f.getStyleClass().add("text-input");
        f.setPrefWidth(w);
        return f;
    }

    // ---------- run / render ----------

    private void run() {
        List<ExecutionStep> trace = session.run(input.get());
        svc.runs().record(info.id(), inputText, trace.size());
        playback.load(trace);
        refresh();
    }

    private void refresh() {
        ExecutionStep s = playback.current();
        int total = playback.total(), i = playback.indexProperty().get();
        updatingScrub = true;
        scrub.setMax(Math.max(0, total - 1));
        scrub.setValue(i);
        updatingScrub = false;
        stepLabel.setText("Step " + (i + 1) + " of " + total);
        percent.setText(total <= 1 ? "100%" : Math.round(i * 100.0 / (total - 1)) + "% complete");
        description.setText(s == null ? "" : s.description());
        description.setWrapText(true);
        if (s != null && s.counters() != null && !s.counters().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            s.counters().forEach((k, v) -> sb.append(sb.length() > 0 ? "   " : "").append(cap(k)).append(": ").append(v));
            counters.setText(sb.toString());
            counters.setVisible(true);
        } else counters.setVisible(false);
        highlightCode(s == null ? -1 : s.codeLine());
        draw();
    }

    private static String cap(String k) {
        return k.isEmpty() ? k : Character.toUpperCase(k.charAt(0)) + k.substring(1);
    }

    private void draw() {
        var g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        ExecutionStep s = playback.current();
        if (s != null && s.state() != null && renderer != null) renderer.draw(g, canvas.getWidth(), canvas.getHeight(), s.state());
    }

    // ---------- code + complexity ----------

    private void buildLower() {
        codeLines.getChildren().clear();
        List<String> lines = info.code() == null ? List.of() : info.code().lines();
        for (int i = 0; i < lines.size(); i++) {
            Label l = new Label(String.format("%2d  %s", i + 1, lines.get(i)));
            l.getStyleClass().add("mono");
            l.setStyle("-fx-text-fill:#F7EFE1;-fx-font-size:12px;");
            l.setMaxWidth(Double.MAX_VALUE);
            codeLines.getChildren().add(l);
        }
        Label codeTitle = label("PSEUDOCODE", "muted");
        codeTitle.setStyle("-fx-text-fill:#E8DCC4;-fx-font-weight:700;");
        VBox code = new VBox(10, codeTitle, codeLines);
        code.getStyleClass().add("code-panel");
        AlgorithmInfo.Complexity c = info.complexity();
        VBox cx = card(label("Complexity", "heading"),
                complexityRow("Best", c.timeBest()), complexityRow("Average", c.timeAverage()),
                complexityRow("Worst", c.timeWorst()), complexityRow("Space", c.spaceAuxiliary()), wrap(c.explanation()));
        HBox r = row(16, code, cx);
        HBox.setHgrow(code, Priority.ALWAYS);
        HBox.setHgrow(cx, Priority.ALWAYS);
        code.setPrefWidth(500);
        cx.setPrefWidth(400);
        lower.getChildren().setAll(r);
    }

    private HBox complexityRow(String k, String v) {
        HBox h = row(10, label(k, "muted"), grow(), label(v, "mono"));
        return h;
    }

    private void highlightCode(int line) {
        for (int i = 0; i < codeLines.getChildren().size(); i++) {
            Node n = codeLines.getChildren().get(i);
            n.setStyle(i + 1 == line
                    ? "-fx-background-color:#9CCBA2;-fx-text-fill:#2B2620;-fx-font-size:12px;-fx-background-radius:6;-fx-padding:1 6 1 6;"
                    : "-fx-text-fill:#F7EFE1;-fx-font-size:12px;-fx-padding:1 6 1 6;");
        }
    }

    // ---------- parsing ----------

    private static List<String> ints(com.fasterxml.jackson.databind.JsonNode n) {
        List<String> out = new ArrayList<>();
        if (n != null && n.isArray()) n.forEach(x -> out.add(x.asText()));
        return out;
    }

    static List<Integer> parseInts(String s) {
        List<Integer> out = new ArrayList<>();
        for (String p : s.split("[,\\s]+")) {
            Integer v = parseInt(p);
            if (v != null) out.add(v);
        }
        return out;
    }

    static Integer parseInt(String s) {
        try { return Integer.valueOf(s.trim()); } catch (RuntimeException e) { return null; }
    }
}
