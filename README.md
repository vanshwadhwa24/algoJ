# AlgoJ

An algorithm and data structure visualizer written in Java. The engines record every step of an algorithm as a trace; the JavaFX UI plays that trace back. Progress is stored in SQLite through JDBC.

## Run

Requires JDK 17+ and Maven.

    mvn javafx:run          # start the app
    mvn test                # run the tests

The database lives at `~/.algoj/algoj.db`. Override it with `-Dalgoj.db=/path/to/file.db`.

## Design

| Package | Role |
| --- | --- |
| `algorithm` | `Algorithm` interface, abstract `SortAlgorithm`, `SearchAlgorithm`, `GraphAlgorithm`, one class per algorithm, `Algorithms` registry |
| `structure` | `StructureEngine<S>` interface, one engine per data structure (stack, queue, BST, AVL, heap, trie, ...), `Structures` registry |
| `model`, `dto` | Immutable-style state snapshots and `ExecutionStep` records |
| `persistence` | `Database` plus DAO interfaces and JDBC implementations: run history, saved inputs, quiz attempts |
| `catalog` | Display data (descriptions, complexity, pseudocode, theory, practice questions) loaded from JSON |
| `ui` | JavaFX screens (Dashboard, Algorithms, Visualizer, Compare, Practice), `Playback`, and `ui.render` with one `StateRenderer` per kind of state |

Adding an algorithm means adding one class and one registry line, plus a catalog entry.

## Tests

- Oracle tests compare algorithm and structure results with trusted references (`java.util` sorting, brute force, etc.).
- Trace invariant tests check intermediate steps, not only the final result.
- Regression tests cover three fixed bugs: Bellman-Ford on undirected graphs, linear queue overflow, heap build.
- Persistence tests check data survives reopening the database.

The JavaFX screens are checked by headless screenshots (`docs/screenshots`), not automated assertions.

## Notes

- Some inputs are clamped by design: factorial up to 7, recursive Fibonacci up to 6, Hanoi 1 to 5 disks, N-Queens 4 to 6.
- Score is 10 points per distinct practice question answered correctly.
