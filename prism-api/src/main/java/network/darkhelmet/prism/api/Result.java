package network.darkhelmet.prism.api;

import network.darkhelmet.prism.api.actions.Handler;

import java.util.List;

public interface Result {

    List<Handler> getActionResults();

    List<Handler> getPaginatedActionResults();

    PrismParameters getParameters();

    int getTotalResults();

    long getQueryTime();

    long getLastTeleportIndex();

    void setLastTeleportIndex(long index);

    int getIndexOfFirstResult();

    int getPage();

    void setPage(int page);

    int getTotalPages();

}
