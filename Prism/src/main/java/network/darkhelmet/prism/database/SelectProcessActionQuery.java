package network.darkhelmet.prism.database;

import network.darkhelmet.prism.actions.PrismProcessAction;

public interface SelectProcessActionQuery extends SelectQuery {

    PrismProcessAction executeProcessQuery();

    void isLastProcessID();

    long getLastProcessIdQuery();
}
