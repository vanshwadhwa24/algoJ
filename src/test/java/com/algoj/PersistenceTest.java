package com.algoj;

import com.algoj.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {

    @TempDir
    Path dir;

    @Test
    void dataSurvivesClosingAndReopeningTheDatabaseFile() {
        Path file = dir.resolve("algoj.db");
        Database first = Database.open(file);
        new JdbcRunHistoryDao(first).record("bubble-sort", "35, 12, 48", 30);
        new JdbcSavedInputDao(first).save("bubble-sort", "demo", "5, 4, 3");
        new JdbcQuizAttemptDao(first).record("q-bubble-1", "bubble-sort", true);

        Database second = Database.open(file); // a fresh connection source, as after an app restart
        assertEquals(1, new JdbcRunHistoryDao(second).count());
        assertEquals("5, 4, 3", new JdbcSavedInputDao(second).forAlgorithm("bubble-sort").get(0).payload());
        assertEquals(1, new JdbcQuizAttemptDao(second).correctQuestionCount());
    }

    @Test
    void savingTheSameNameReplacesTheOldInput() {
        SavedInputDao dao = new JdbcSavedInputDao(Database.open(dir.resolve("a.db")));
        dao.save("merge-sort", "mine", "1, 2");
        dao.save("merge-sort", "mine", "3, 4");
        List<SavedInput> all = dao.forAlgorithm("merge-sort");
        assertEquals(1, all.size());
        assertEquals("3, 4", all.get(0).payload());
        dao.delete(all.get(0).id());
        assertTrue(dao.forAlgorithm("merge-sort").isEmpty());
    }

    @Test
    void aQuestionOnlyEarnsPointsOnceEvenIfAnsweredAgain() {
        QuizAttemptDao dao = new JdbcQuizAttemptDao(Database.open(dir.resolve("q.db")));
        assertFalse(dao.record("q1", "bfs", false));
        assertTrue(dao.record("q1", "bfs", true));
        assertFalse(dao.record("q1", "bfs", true));
        assertEquals(1, dao.correctQuestionCount());
        assertEquals(3, dao.attemptCount());
    }

    @Test
    void recentRunsComeNewestFirst() {
        RunHistoryDao dao = new JdbcRunHistoryDao(Database.open(dir.resolve("r.db")));
        dao.record("a", "1", 1);
        dao.record("b", "2", 2);
        assertEquals("b", dao.recent(10).get(0).algorithmId());
    }
}
