package network.darkhelmet.prism.api.actions;

import network.darkhelmet.prism.api.actions.types.ActionType;

public interface IAction {
    /**
     * Apply the rollback. If the action type is not reversible, this does nothing.
     */
    void applyRollback();

    /**
     * Apply the restore. If the action type is not reversible, this does nothing.
     */
    void applyRestore();

    /**
     * Get the action type.
     *
     * @return The action type
     */
    ActionType type();
}
