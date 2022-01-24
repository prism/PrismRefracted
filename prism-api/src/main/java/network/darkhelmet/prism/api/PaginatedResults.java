package network.darkhelmet.prism.api;

import java.util.List;

public class PaginatedResults<T> {
    /**
     * The segmented results.
     */
    List<T> results;

    /**
     * Construct a new paginated results object.
     *
     * @param results A list of results
     */
    public PaginatedResults(List<T> results) {
        this.results = results;
    }

    /**
     * Check if the results are empty.
     *
     * @return True if no results
     */
    public boolean isEmpty() {
        return results.isEmpty();
    }

    /**
     * Get the results.
     *
     * @return The results
     */
    public List<T> results() {
        return results;
    }
}
