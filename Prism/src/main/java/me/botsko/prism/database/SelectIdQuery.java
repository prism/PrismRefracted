package me.botsko.prism.database;

public interface SelectIdQuery extends SelectQuery {

    void setMax();

    void setMin();

    void setMinMax();

    long[] execute();
}
