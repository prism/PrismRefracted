package network.darkhelmet.prism.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import network.darkhelmet.prism.api.actions.ActionResultType;
import network.darkhelmet.prism.api.actions.ActionType;

public class ActionRegistry {
    /**
     * Cache of action types by key.
     */
    private final Map<String, ActionType> actionsTypes = new HashMap<>();

    /**
     * Static cache of Prism action types.
     */
    public static final ActionType BLOCK_BREAK = new ActionType("block-break", ActionResultType.REMOVES);

    /**
     * Construct the action registry.
     */
    public ActionRegistry() {
        // Register Prism actions
        registerAction(BLOCK_BREAK);
    }

    /**
     * Register a new action type.
     *
     * @param actionType The action type
     */
    public void registerAction(ActionType actionType) {
        if (actionsTypes.containsKey(actionType.key())) {
            throw new IllegalArgumentException("Registry already has an action type with that key.");
        }

        actionsTypes.put(actionType.key(), actionType);
    }

    /**
     * Get an action type by key.
     *
     * @param key The key
     * @return The action type, if any
     */
    public Optional<ActionType> getActionType(String key) {
        if (actionsTypes.containsKey(key)) {
            return Optional.of(actionsTypes.get(key));
        }

        return Optional.empty();
    }
}
