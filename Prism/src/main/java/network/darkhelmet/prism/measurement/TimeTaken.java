package network.darkhelmet.prism.measurement;

import network.darkhelmet.prism.Prism;

import java.util.Calendar;
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
     * Get the timestamp.
     * @return long
     */
    protected long getTimestamp() {
        final Calendar lCDateTime = Calendar.getInstance();
        return lCDateTime.getTimeInMillis();
    }

    /**
     * Get the event.
     * @param eventname String
     */
    public void recordTimedEvent(String eventname) {
        if (!Prism.isDebug()) {
            return;
        }
        eventsTimed.put(getTimestamp(), eventname);
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
                Prism.debug("-- Timer information for last action: --");
                for (final Entry<Long, String> entry : timers.entrySet()) {
                    long diff = 0;
                    if (lastTime > 0) {
                        diff = entry.getKey() - lastTime;
                        total += diff;
                    }
                    Prism.debug(entry.getValue() + " " + diff + "ms");
                    lastTime = entry.getKey();
                }
                Prism.debug("Total time: " + total + "ms");
            }
        }
        plugin.eventTimer.resetEventList();
    }
}
