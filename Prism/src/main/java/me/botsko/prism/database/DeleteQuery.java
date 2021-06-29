package me.botsko.prism.database;

public interface DeleteQuery extends SelectQuery {
    /**
     * the number of affected rows.
     * @return int
     */
    int execute();

    void setShouldPause(boolean pause);
}
