package network.darkhelmet.prism.api.actions;

public interface Reversible {
    /**
     * Rollback (reverse) the results of this game action.
     */
    void applyRollback();

    /**
     * Restore (re-apply) the results of this game action.
     */
    void applyRestore();
}
