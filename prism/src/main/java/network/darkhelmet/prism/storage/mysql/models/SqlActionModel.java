package network.darkhelmet.prism.storage.mysql.models;

import java.util.Objects;

import network.darkhelmet.prism.api.storage.models.ActionModel;

public class SqlActionModel extends ActionModel {
    /**
     * The primary key.
     */
    private Long id;

    /**
     * Construct a sql action model.
     *
     * @param id The id
     * @param actionKey The action key
     */
    public SqlActionModel(Long id, String actionKey) {
        super(actionKey);

        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        var that = (SqlActionModel) obj;
        return Objects.equals(this.id, that.id);
    }

    /**
     * The ID.
     *
     * @return The primary key
     */
    public Long id() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key);
    }

    @Override
    public String toString() {
        return "SqlActionModel["
            + "id=" + id + ","
            + "key=" + key + ']';
    }
}