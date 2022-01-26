package network.darkhelmet.prism.api.actions.types;

import network.darkhelmet.prism.api.actions.ActionData;
import network.darkhelmet.prism.api.actions.IAction;

public interface IActionType {
    /**
     * Creates an action given the action data.
     *
     * @param actionData The action data
     * @return The action
     */
    IAction createAction(ActionData actionData);
}
