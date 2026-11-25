package com.algoj.ui;

import com.algoj.catalog.Catalog;
import com.algoj.persistence.*;

/** The app's shared collaborators: catalog data and the JDBC-backed DAOs. Screens depend on the interfaces only. */
public record Services(Catalog catalog, RunHistoryDao runs, SavedInputDao savedInputs, QuizAttemptDao quizzes) {
    public static Services open(java.nio.file.Path dbFile) {
        Database db = Database.open(dbFile);
        return new Services(Catalog.load(), new JdbcRunHistoryDao(db), new JdbcSavedInputDao(db), new JdbcQuizAttemptDao(db));
    }
}
