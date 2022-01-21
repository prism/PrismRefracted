package network.darkhelmet.prism.api.actions;

public interface ActionType {


    boolean canRollback();

    boolean canRestore();

    Class<? extends Handler> getHandler();

    /**
     *  Checks if an Action requires a specific handler.
     * @param handler Handler
     * @return boolean
     */
    boolean requiresHandler(Class<? extends Handler> handler);

    boolean doesCreateBlock();

    String getName();

    /**
     * Get the Class Short name.
     * @return String
     */
    String getShortName();

}
