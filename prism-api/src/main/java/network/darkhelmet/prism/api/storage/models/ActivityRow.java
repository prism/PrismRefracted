package network.darkhelmet.prism.api.storage.models;

public record ActivityRow(String action, String cause, Integer timestamp, String material) {
    /**
     * Get the action family. "break" for "block-break"
     *
     * @return The action family
     */
    public String actionFamily() {
        String[] segments = action.split("-");

        return segments[segments.length - 1];
    }

    /**
     * Get the shorthand syntax for time since.
     *
     * @return The time since
     */
    public String since() {
        long diffInSeconds = System.currentTimeMillis() / 1000 - timestamp;

        if (diffInSeconds < 60) {
            // @todo languagize me
            return "just now";
        }

        long period = 24 * 60 * 60;

        final long[] diff = {
            diffInSeconds / period,
            (diffInSeconds / (period /= 24)) % 24,
            (diffInSeconds / (period / 60)) % 60
        };

        StringBuilder timeAgo = new StringBuilder();

        if (diff[0] > 0) {
            timeAgo.append(diff[0]).append('d');
        }

        if (diff[1] > 0) {
            timeAgo.append(diff[1]).append('h');
        }

        if (diff[2] > 0) {
            timeAgo.append(diff[2]).append('m');
        }

        // 'time_ago' will have something at this point, because if all 'diff's
        // were 0, the first if check would have caught and returned "just now"
        // @todo languagize me
        return timeAgo.append(" ago").toString();
    }
}
