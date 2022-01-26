package network.darkhelmet.prism.api.actions.types;

public enum ActionResultType {
    /**
     * Actions which result in a creation.
     *
     * <p>Example: block place.</p>
     */
    CREATES,

    /**
     * Actions which have no "real" result and are purely informational. (Or can't be realistically reversed).
     *
     * <p>Example: vehicle enter</p>
     */
    NONE,

    /**
     * Actions which result in a removal.
     *
     * <p>Example: block broken, item removed, etc.</p>
     */
    REMOVES
}
