package network.darkhelmet.prism.api.actions;

import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;

public interface IAction {
    /**
     * Apply the rollback. If the action type is not reversible, this does nothing.
     *
     * @param activityContext The activity as a context
     */
    void applyRollback(IActivity activityContext);

    /**
     * Apply the restore. If the action type is not reversible, this does nothing.
     *
     * @param activityContext The activity as a context
     */
    void applyRestore(IActivity activityContext);

    /**
     * Get the action type.
     *
     * @return The action type
     */
    ActionType type();
}
