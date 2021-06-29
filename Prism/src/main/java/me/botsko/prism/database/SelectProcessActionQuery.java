package me.botsko.prism.database;

import me.botsko.prism.actions.PrismProcessAction;

public interface SelectProcessActionQuery extends SelectQuery {

    PrismProcessAction executeProcessQuery();

    void isLastProcessID();

    long getLastProcessIdQuery();
}
