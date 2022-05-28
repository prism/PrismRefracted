package network.darkhelmet.prism.measurement;

import network.darkhelmet.prism.Prism;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TimeTaken {

    protected final Prism plugin;

    protected final TreeMap<Long, String> eventsTimed = new TreeMap<>();

    /**
     * Constructor.
     * @param plugin Prism
     */
    public TimeTaken(Prism plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the event.
     * @param eventname String
     */
    public void recordTimedEvent(String eventname) {
        if (!Prism.isDebug()) {
            return;
        }
        eventsTimed.put(System.nanoTime(), eventname);
    }

    protected void resetEventList() {
        eventsTimed.clear();
    }

    protected TreeMap<Long, String> getEventsTimedList() {
        return eventsTimed;
    }

    /**
     * Print the record.
     */
    public void printTimeRecord() {

        // record timed events to log
        if (Prism.isDebug()) {
            final TreeMap<Long, String> timers = plugin.eventTimer.getEventsTimedList();
            if (timers.size() > 0) {
                long lastTime = 0;
                long total = 0;
                Prism.debug("-- 上个操作的计时信息: --");
                for (final Entry<Long, String> entry : timers.entrySet()) {
                    long diff = 0;
                    if (lastTime > 0) {
                        diff = entry.getKey() - lastTime;
                        total += diff;
                    }
                    Prism.debug(entry.getValue() + ": " + new BigDecimal(diff / 1_000_000_000f).toPlainString() + "s");
                    lastTime = entry.getKey();
                }
                Prism.debug("总计时长: " + new BigDecimal(total / 1_000_000_000f).toPlainString() + "s");
            }
        }
        plugin.eventTimer.resetEventList();
    }
}
