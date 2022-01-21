package network.darkhelmet.prism.purge;

import network.darkhelmet.prism.actionlibs.QueryParameters;

public interface PurgeCallback {
    void cycle(QueryParameters param, int cycleRowsAffected, int totalRecordsAffected,
               boolean cycleComplete, long maxCycleTime);
}