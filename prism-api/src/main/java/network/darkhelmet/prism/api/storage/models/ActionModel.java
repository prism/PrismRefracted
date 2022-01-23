package network.darkhelmet.prism.api.storage.models;

import java.util.Objects;

public class ActionModel {
    /**
     * The world UUID.
     */
    protected final String key;

    /**
     * Construct a new action model.
     *
     * @param key The action key
     */
    public ActionModel(String key) {
        this.key = key;
    }

    /**
     * Get the action key.
     *
     * @return The key
     */
    public String key() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        var that = (ActionModel) obj;
        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "ActionModel["
            + "key=" + key + ']';
    }
}