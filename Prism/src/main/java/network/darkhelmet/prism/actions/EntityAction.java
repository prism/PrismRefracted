package network.darkhelmet.prism.actions;

import com.google.gson.JsonObject;
import io.github.rothes.prismcn.CNLocalization;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actions.entity.EntitySerializer;
import network.darkhelmet.prism.actions.entity.EntitySerializerFactory;
import network.darkhelmet.prism.api.ChangeResult;
import network.darkhelmet.prism.api.ChangeResultType;
import network.darkhelmet.prism.api.PrismParameters;
import network.darkhelmet.prism.appliers.ChangeResultImpl;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EntityAction extends GenericAction {

    private EntitySerializer serializer;

    /**
     * Constructor.
     * @param name String
     * @return EntityType
     */
    @Nullable
    public static EntityType getEntityType(String name) {
        try {
            return EntityType.valueOf(CNLocalization.restoreEntityLocale(name.toUpperCase(Locale.ROOT)));
        } catch (final IllegalArgumentException e) {
            // In pre-RC builds we logged the wrong name of entities, sometimes
            // the names
            // don't match the enum.
            Prism.debug("无法获取 EntityType, 传参为 " + name + ", 英文为 " + CNLocalization.restoreEntityLocale(name.toUpperCase(Locale.ROOT)));
        }
        return null;
    }

    /**
     * Set the entity.
     * @param entity Entity
     * @param dyeUsed String
     */
    public void setEntity(Entity entity, String dyeUsed) {

        // Build an object for the specific details of this action
        if (entity != null && entity.getType() != null && entity.getType().name() != null) {
            setLoc(entity.getLocation());

            serializer = EntitySerializerFactory.getSerializer(entity.getType());
            serializer.serialize(entity);
            serializer.setNewColor(dyeUsed);
        }
    }

    @Override
    public String getCustomDesc() {
        if (serializer != null) {
            return serializer.customDesc();
        }

        return null;
    }

    @Override
    public boolean hasExtraData() {
        return serializer != null;
    }

    @Override
    public String serialize() {
        return gson().toJson(serializer);
    }

    @Override
    public void deserialize(String data) {
        if (data != null && data.startsWith("{")) {
            String entityName = gson().fromJson(data, JsonObject.class).get("entityName").getAsString();
            serializer = gson().fromJson(data, EntitySerializerFactory.getSerlializingClass(getEntityType(entityName)));
        }
    }

    /**
     * Get nice name.
     * @return String
     */
    @Override
    public String getNiceName() {
        if (serializer != null) {
            return serializer.toString();
        }

        return "未知";
    }

    @Override
    public ChangeResult applyRollback(Player player, PrismParameters parameters, boolean isPreview) {
        if (serializer == null) {
            return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
        }

        EntityType entityType = getEntityType(serializer.getEntityName());
        if (entityType != null && !Prism.getIllegalEntities().contains(entityType)) {
            if (!isPreview) {
                final Location loc = getLoc().add(0.5, 0.0, 0.5);
                if (entityType.getEntityClass() != null && loc.getWorld() != null) {
                    loc.getWorld().spawn(loc, entityType.getEntityClass(), entity -> serializer.deserialize(entity));
                } else {
                    return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
                }
                return new ChangeResultImpl(ChangeResultType.APPLIED, null);

            } else {
                return new ChangeResultImpl(ChangeResultType.PLANNED, null);
            }
        } else {
            return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
        }
    }
}
