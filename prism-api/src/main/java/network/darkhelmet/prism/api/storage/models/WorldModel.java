package network.darkhelmet.prism.api.storage.models;

import java.util.Objects;
import java.util.UUID;

public class WorldModel {
    /**
     * The world UUID.
     */
    protected final UUID worldUuid;

    /**
     * Construct a new world model.
     *
     * @param worldUuid The world UUID.
     */
    public WorldModel(UUID worldUuid) {
        this.worldUuid = worldUuid;
    }

    /**
     * Get the world UUID.
     *
     * @return The world UUID.
     */
    public UUID worldUuid() {
        return worldUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        var that = (WorldModel) obj;
        return Objects.equals(this.worldUuid, that.worldUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldUuid);
    }

    @Override
    public String toString() {
        return "WorldModel["
            + "worldUUID=" + worldUuid + ']';
    }
}
