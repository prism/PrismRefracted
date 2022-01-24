package network.darkhelmet.prism.storage.mysql.models;

import java.util.Objects;
import java.util.UUID;

import network.darkhelmet.prism.api.storage.models.WorldModel;

public class SqlWorldModel extends WorldModel {
    /**
     * The primary key.
     */
    private long id;

    /**
     * Construct a sql world model.
     *
     * @param id The id
     * @param worldUuid The world UUID
     */
    public SqlWorldModel(long id, UUID worldUuid) {
        super(worldUuid);

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

        var that = (SqlWorldModel) obj;
        return Objects.equals(this.id, that.id);
    }

    /**
     * The ID.
     *
     * @return The primary key
     */
    public long id() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, worldUuid);
    }

    @Override
    public String toString() {
        return "SqlWorldModel["
            + "id=" + id + ","
            + "worldUUID=" + worldUuid + ']';
    }
}
