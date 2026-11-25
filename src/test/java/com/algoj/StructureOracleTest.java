package com.algoj;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.LinearStructureModel.*;
import com.algoj.model.TreeStructureModel.*;
import com.algoj.structure.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.algoj.TestSupport.Failures;
import static org.junit.jupiter.api.Assertions.*;

/** Applies random operation sequences to each structure and compares with java.util references. */
class StructureOracleTest {

    private final Random random = new Random(7);

    @SuppressWarnings("unchecked")
    private static <S> S next(StructureEngine<S> engine, S state, Operation op) {
        List<ExecutionStep> steps = engine.apply(state, op);
        return (S) steps.get(steps.size() - 1).state();
    }

    private static void inorder(BSTNode n, List<Integer> out) {
        if (n == null) return;
        inorder(n.getLeft(), out);
        out.add(n.getValue());
        inorder(n.getRight(), out);
    }

    private static void inorder(AVLNode n, List<Integer> out) {
        if (n == null) return;
        inorder(n.getLeft(), out);
        out.add(n.getValue());
        inorder(n.getRight(), out);
    }

    private static int height(AVLNode n) {
        return n == null ? 0 : 1 + Math.max(height(n.getLeft()), height(n.getRight()));
    }

    private static String avlProblem(AVLNode n) {
        if (n == null) return null;
        if (Math.abs(height(n.getLeft()) - height(n.getRight())) > 1) return "unbalanced at " + n.getValue();
        if (n.getHeight() != height(n)) return "stored height wrong at " + n.getValue();
        String left = avlProblem(n.getLeft());
        return left != null ? left : avlProblem(n.getRight());
    }

    private static boolean isHeap(List<Integer> a, boolean max) {
        for (int i = 1; i < a.size(); i++) {
            int p = (i - 1) / 2;
            if (max ? a.get(p) < a.get(i) : a.get(p) > a.get(i)) return false;
        }
        return true;
    }

    @Test
    void stackMatchesReference() {
        Failures f = new Failures();
        StackStructureEngine engine = new StackStructureEngine();
        for (int it = 0; it < 100; it++) {
            StackDSState s = engine.createInitial(List.of(1, 2, 3), 5);
            List<Integer> ref = new ArrayList<>(List.of(1, 2, 3));
            for (int i = 0; i < 15; i++) {
                int v = random.nextInt(50);
                String op = new String[]{"push", "pop", "peek"}[random.nextInt(3)];
                s = next(engine, s, Operation.withValue(op, v));
                if (op.equals("push") && ref.size() < 5) ref.add(v);
                if (op.equals("pop") && !ref.isEmpty()) ref.remove(ref.size() - 1);
                List<Integer> got = new ArrayList<>();
                s.getItems().forEach(x -> got.add(x.getValue()));
                if (!got.equals(ref)) {
                    f.add("stack", op + v + " got " + got + " expected " + ref);
                    break;
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void queuesMatchReferenceInBothModes() {
        Failures f = new Failures();
        QueueStructureEngine engine = new QueueStructureEngine();
        for (boolean circular : new boolean[]{false, true}) {
            String key = circular ? "circular-queue" : "linear-queue";
            for (int it = 0; it < 150; it++) {
                QueueDSState s = engine.createInitial(List.of(1, 2, 3), 5, circular);
                List<Integer> ref = new ArrayList<>(List.of(1, 2, 3));
                int rearIndex = 2; // linear mode: rear never wraps, so freed front slots stay unusable
                for (int i = 0; i < 20; i++) {
                    int v = random.nextInt(50);
                    boolean enqueue = random.nextBoolean();
                    s = next(engine, s, Operation.withValue(enqueue ? "enqueue" : "dequeue", v));
                    if (enqueue) {
                        boolean accepted = circular ? ref.size() < 5 : ref.isEmpty() ? true : rearIndex < 4;
                        if (accepted) {
                            if (!circular && ref.isEmpty()) rearIndex = 0; else if (!circular) rearIndex++;
                            ref.add(v);
                        }
                    } else if (!ref.isEmpty()) {
                        ref.remove(0);
                    }
                    List<Integer> got = new ArrayList<>();
                    for (int j = 0; j < s.getSize(); j++) {
                        int idx = circular ? (s.getFront() + j) % s.getCapacity() : s.getFront() + j;
                        got.add(idx < s.getSlots().size() ? s.getSlots().get(idx).getValue() : -1);
                    }
                    if (!got.equals(ref)) {
                        f.add(key, "got " + got + " expected " + ref + " front=" + s.getFront() + " rear=" + s.getRear());
                        break;
                    }
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    /** Regression: a linear queue used to wrap its rear pointer like a circular queue. */
    @Test
    void linearQueueReportsOverflowWhenRearReachesTheEnd() {
        QueueStructureEngine engine = new QueueStructureEngine();
        QueueDSState s = engine.createInitial(List.of(1, 2, 3, 4, 5), 5, false);
        s = next(engine, s, Operation.of("dequeue"));
        List<ExecutionStep> steps = engine.apply(s, Operation.withValue("enqueue", 9));
        assertEquals("overflow", steps.get(steps.size() - 1).operationType());
        assertEquals(4, ((QueueDSState) steps.get(steps.size() - 1).state()).getSize());
    }

    @Test
    void linkedListMatchesReference() {
        Failures f = new Failures();
        LinkedListStructureEngine engine = new LinkedListStructureEngine();
        for (boolean doubly : new boolean[]{false, true}) {
            for (int it = 0; it < 100; it++) {
                LinkedListDSState s = engine.createInitial(List.of(1, 2, 3), doubly);
                List<Integer> ref = new ArrayList<>(List.of(1, 2, 3));
                for (int i = 0; i < 12; i++) {
                    int v = random.nextInt(50), pos = random.nextInt(ref.size() + 1);
                    String op = new String[]{"insertHead", "insertTail", "insertAt", "deleteHead", "deleteTail", "deleteAt", "reverse", "search", "traverse"}[random.nextInt(9)];
                    if ((op.equals("deleteAt")) && pos >= ref.size()) pos = Math.max(0, ref.size() - 1);
                    s = next(engine, s, new Operation(op, v, pos, null, null));
                    switch (op) {
                        case "insertHead" -> ref.add(0, v);
                        case "insertTail" -> ref.add(v);
                        case "insertAt" -> ref.add(Math.min(pos, ref.size()), v);
                        case "deleteHead" -> { if (!ref.isEmpty()) ref.remove(0); }
                        case "deleteTail" -> { if (!ref.isEmpty()) ref.remove(ref.size() - 1); }
                        case "deleteAt" -> { if (!ref.isEmpty()) ref.remove(pos); }
                        case "reverse" -> Collections.reverse(ref);
                        default -> { }
                    }
                    List<Integer> got = new ArrayList<>();
                    s.getNodes().forEach(n -> got.add(n.getValue()));
                    if (!got.equals(ref)) {
                        f.add("linked-list", op + "(" + v + "," + pos + ") got " + got + " expected " + ref);
                        break;
                    }
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void arrayMatchesReference() {
        Failures f = new Failures();
        ArrayStructureEngine engine = new ArrayStructureEngine();
        for (int it = 0; it < 100; it++) {
            ArrayDSState s = engine.createInitial(List.of(10, 20, 30));
            List<Integer> ref = new ArrayList<>(List.of(10, 20, 30));
            for (int i = 0; i < 10; i++) {
                int v = random.nextInt(50), idx = random.nextInt(ref.size() + 1);
                String op = new String[]{"insert", "delete", "update", "access", "search", "traverse"}[random.nextInt(6)];
                if (!op.equals("insert") && idx >= ref.size()) idx = Math.max(0, ref.size() - 1);
                s = next(engine, s, new Operation(op, v, idx, null, null));
                switch (op) {
                    case "insert" -> ref.add(idx, v);
                    case "delete" -> { if (!ref.isEmpty()) ref.remove(idx); }
                    case "update" -> { if (!ref.isEmpty()) ref.set(idx, v); }
                    default -> { }
                }
                List<Integer> got = new ArrayList<>();
                s.getCells().forEach(c -> got.add(c.getValue()));
                if (!got.equals(ref)) {
                    f.add("array", op + "(i" + idx + ",v" + v + ") got " + got + " expected " + ref);
                    break;
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void hashTableMatchesReference() {
        Failures f = new Failures();
        HashTableStructureEngine engine = new HashTableStructureEngine();
        for (String strategy : List.of("chaining", "probing")) {
            for (int it = 0; it < 100; it++) {
                HashTableDSState s = engine.createInitial(strategy, 7, Collections.singletonList(new String[]{"apple", "1"}));
                Map<String, String> ref = new HashMap<>(Map.of("apple", "1"));
                for (int i = 0; i < 10; i++) {
                    String key = "k" + random.nextInt(8), value = "" + random.nextInt(99);
                    String op = new String[]{"insert", "delete", "search"}[random.nextInt(3)];
                    s = next(engine, s, new Operation(op, null, null, key, value));
                    if (op.equals("insert") && (ref.size() < 7 || ref.containsKey(key))) ref.put(key, value);
                    if (op.equals("delete")) ref.remove(key);
                    Map<String, String> got = new HashMap<>();
                    for (HashBucket b : s.getBuckets())
                        for (HashEntry e : b.getEntries())
                            if (!Boolean.TRUE.equals(e.getIsDeleted()) && e.getKey() != null) got.put(e.getKey(), e.getValue());
                    if (!got.equals(ref)) {
                        f.add("hash-" + strategy, op + "(" + key + "," + value + ") got " + got + " expected " + ref);
                        break;
                    }
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void bstLevelOrderAndExtremesMatchReference() {
        BSTStructureEngine engine = new BSTStructureEngine();
        for (int it = 0; it < 100; it++) {
            BSTDSState s = engine.createInitial(List.of(50));
            TreeSet<Integer> ref = new TreeSet<>(List.of(50));
            for (int i = 0; i < 10; i++) {
                int v = random.nextInt(40) * 3;
                s = next(engine, s, Operation.withValue("insert", v));
                ref.add(v);
            }
            // reference breadth-first walk over the model's own links
            List<Integer> bfs = new ArrayList<>();
            ArrayDeque<BSTNode> q = new ArrayDeque<>(List.of(s.getRoot()));
            while (!q.isEmpty()) {
                BSTNode n = q.poll();
                bfs.add(n.getValue());
                if (n.getLeft() != null) q.add(n.getLeft());
                if (n.getRight() != null) q.add(n.getRight());
            }
            assertEquals(bfs, next(engine, s, Operation.of("levelorder")).getTraversalOutput());
            assertEquals("The minimum is " + ref.first() + ".", next(engine, s, Operation.of("findMin")).getMessage());
            assertEquals("The maximum is " + ref.last() + ".", next(engine, s, Operation.of("findMax")).getMessage());
        }
        BSTDSState fixed = engine.createInitial(List.of(50, 30, 70, 20, 40, 60, 80));
        assertEquals(List.of(50, 30, 70, 20, 40, 60, 80), next(engine, fixed, Operation.of("levelorder")).getTraversalOutput());
        assertEquals("Tree is empty: no minimum.", next(engine, engine.createInitial(List.of()), Operation.of("findMin")).getMessage());
    }

    @Test
    void binarySearchTreeMatchesReferenceAndTraversals() {
        Failures f = new Failures();
        BSTStructureEngine engine = new BSTStructureEngine();
        for (int it = 0; it < 150; it++) {
            BSTDSState s = engine.createInitial(List.of(50, 30, 70));
            TreeSet<Integer> ref = new TreeSet<>(List.of(50, 30, 70));
            for (int i = 0; i < 12; i++) {
                int v = random.nextInt(20) * 5;
                String op = new String[]{"insert", "delete", "search", "inorder", "preorder", "postorder"}[random.nextInt(6)];
                s = next(engine, s, Operation.withValue(op, v));
                if (op.equals("insert")) ref.add(v);
                if (op.equals("delete")) ref.remove(v);
                List<Integer> got = new ArrayList<>();
                inorder(s.getRoot(), got);
                if (!got.equals(new ArrayList<>(ref)) || s.getNodeCount() != ref.size()) {
                    f.add("bst", op + v + " got " + got + " expected " + ref);
                    break;
                }
            }
        }
        BSTDSState fixed = engine.createInitial(List.of(50, 30, 70, 20, 40, 60, 80));
        assertEquals(List.of(20, 30, 40, 50, 60, 70, 80), next(engine, fixed, Operation.of("inorder")).getTraversalOutput());
        assertEquals(List.of(50, 30, 20, 40, 70, 60, 80), next(engine, fixed, Operation.of("preorder")).getTraversalOutput());
        assertEquals(List.of(20, 40, 30, 60, 80, 70, 50), next(engine, fixed, Operation.of("postorder")).getTraversalOutput());
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void avlStaysBalancedAndOrdered() {
        Failures f = new Failures();
        AVLStructureEngine engine = new AVLStructureEngine();
        for (int it = 0; it < 150; it++) {
            AVLDSState s = engine.createInitial(List.of(30, 20, 40));
            TreeSet<Integer> ref = new TreeSet<>(List.of(30, 20, 40));
            for (int i = 0; i < 12; i++) {
                int v = random.nextInt(40);
                s = next(engine, s, Operation.withValue("insert", v));
                ref.add(v);
                List<Integer> got = new ArrayList<>();
                inorder(s.getRoot(), got);
                String problem = avlProblem(s.getRoot());
                if (!got.equals(new ArrayList<>(ref)) || problem != null) {
                    f.add("avl", "insert " + v + " got " + got + " " + problem);
                    break;
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void heapsMatchPriorityQueue() {
        Failures f = new Failures();
        HeapStructureEngine engine = new HeapStructureEngine();
        for (String type : List.of("max", "min")) {
            for (int it = 0; it < 150; it++) {
                List<Integer> seed = List.of(5, 3, 8, 1, 9); // deliberately not a heap
                HeapDSState s = engine.createInitial(type, seed);
                PriorityQueue<Integer> ref = new PriorityQueue<>(type.equals("max") ? Comparator.<Integer>reverseOrder() : Comparator.<Integer>naturalOrder());
                ref.addAll(seed);
                for (int i = 0; i < 14; i++) {
                    int v = random.nextInt(50);
                    String op = new String[]{"insert", "extract", "peek"}[random.nextInt(3)];
                    s = next(engine, s, Operation.withValue(op, v));
                    if (op.equals("insert")) ref.add(v);
                    if (op.equals("extract")) ref.poll();
                    List<Integer> got = new ArrayList<>();
                    s.getArray().forEach(e -> got.add(e.getValue()));
                    List<Integer> sortedGot = new ArrayList<>(got), sortedRef = new ArrayList<>(ref);
                    Collections.sort(sortedGot);
                    Collections.sort(sortedRef);
                    if (!sortedGot.equals(sortedRef) || !isHeap(got, type.equals("max"))) {
                        f.add("heap-" + type, op + v + " got " + got);
                        break;
                    }
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void trieMatchesWordSetAndPrunesBranches() {
        Failures f = new Failures();
        TrieStructureEngine engine = new TrieStructureEngine();
        String[] words = {"cat", "car", "cart", "dog", "do", "d"};
        for (int it = 0; it < 150; it++) {
            TrieDSState s = engine.createInitial(List.of("cat", "car"));
            TreeSet<String> ref = new TreeSet<>(List.of("cat", "car"));
            for (int i = 0; i < 10; i++) {
                String w = words[random.nextInt(words.length)];
                String op = new String[]{"insert", "delete", "search"}[random.nextInt(3)];
                s = next(engine, s, new Operation(op, null, null, null, w));
                if (op.equals("insert")) ref.add(w);
                if (op.equals("delete")) ref.remove(w);
                if (!new TreeSet<>(s.getWordsList()).equals(ref) || s.getWordCount() != ref.size() || hasDeadBranch(s.getRoot(), true)) {
                    f.add("trie", op + " " + w + " got " + s.getWordsList() + " expected " + ref);
                    break;
                }
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    private static boolean hasDeadBranch(TrieNode n, boolean root) {
        if (n == null) return false;
        if (!root && n.getChildren().isEmpty() && !n.isEndOfWord()) return true;
        for (TrieNode c : n.getChildren().values()) if (hasDeadBranch(c, false)) return true;
        return false;
    }
}
