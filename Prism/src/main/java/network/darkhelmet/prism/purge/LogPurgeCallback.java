package network.darkhelmet.prism.purge;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;

public class LogPurgeCallback implements PurgeCallback {

    /**
     * Simply log the purges, being done automatically.
     */
    @Override
    public void cycle(QueryParameters param, int cycleRowsAffected, int totalRecordsAffected,
                      boolean cycleComplete, long maxCycleTime) {
        Prism.debug("周期数据清理任务清理了 " + cycleRowsAffected + " 行.");
        if (cycleComplete) {
            Prism.log("清理了 " + totalRecordsAffected + " 行. 最大周期时间为 " + maxCycleTime + " 毫秒. 使用:"
                    + param.getOriginalCommand());
        }
    }
}