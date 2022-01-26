package network.darkhelmet.prism.actions;

import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IAction;

public abstract class Action implements IAction {
    /**
     * The type.
     */
    private ActionType type;

    /**
     * Construct a new action.
     *
     * @param type The action type
     */
    public Action(ActionType type) {
        this.type = type;
    }

    /**
     * Get the action type.
     *
     * @return The action type
     */
    public ActionType type() {
        return type;
    }
}
