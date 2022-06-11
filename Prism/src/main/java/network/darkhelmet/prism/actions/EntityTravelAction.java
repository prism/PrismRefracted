package network.darkhelmet.prism.actions;

import io.github.rothes.prismcn.CNLocalization;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class EntityTravelAction extends GenericAction {
    protected EntityTravelActionData actionData;

    public EntityTravelAction() {
        actionData = new EntityTravelActionData();
    }

    /**
     * Set entity.
     * @param entity Entity.
     */
    public void setEntity(Entity entity) {
        if (entity != null) {
            if (entity instanceof Player) {
                setPlayer((Player) entity);
            } else {
                setSourceName(CNLocalization.getEntityLocale(entity.getType()));
            }
        }
    }

    /**
     * Set Location .
     * @param to Location.
     */
    public void setToLocation(Location to) {
        if (to != null) {
            actionData.x = to.getBlockX();
            actionData.y = to.getBlockY();
            actionData.z = to.getBlockZ();
        }
    }

    /**
     * Set cause.
     * @param cause TeleportCause
     */
    public void setCause(TeleportCause cause) {
        if (cause != null) {
            switch (cause) {
                case NETHER_PORTAL:
                    actionData.cause = "下界传送门";
                    break;
                case END_PORTAL:
                    actionData.cause = "末地传送门";
                    break;
                case COMMAND:
                    actionData.cause = "指令";
                    break;
                case PLUGIN:
                    actionData.cause = "插件";
                    break;
                case UNKNOWN:
                    actionData.cause = "未知";
                    break;
                case SPECTATE:
                    actionData.cause = "观察者";
                    break;
                case END_GATEWAY:
                    actionData.cause = "末地折跃门";
                    break;
                case ENDER_PEARL:
                    actionData.cause = "末影珍珠";
                    break;
                case CHORUS_FRUIT:
                    actionData.cause = "紫颂果";
                    break;
                default:
                    actionData.cause = cause.name().toLowerCase();
            }
        }
    }

    @Override
    public boolean hasExtraData() {
        return actionData != null;
    }

    @Override
    public String serialize() {
        return gson().toJson(actionData);
    }

    @Override
    public void deserialize(String data) {
        if (data != null && data.startsWith("{")) {
            actionData = gson().fromJson(data, EntityTravelActionData.class);
        }
    }

    /**
     * Set TravelActionData.
     * @return EntityTravelActionData
     */
    @SuppressWarnings("unused")
    public EntityTravelActionData getActionData() {
        return actionData;
    }

    /**
     * Get nice name.
     * @return String
     */
    @Override
    public String getNiceName() {
        if (actionData != null) {
            final String cause = (actionData.cause == null ? "未知方法" : actionData.cause.replace("_", " "));
            return "通过 " + cause + " 传送到了 " + actionData.x + " " + actionData.y + " " + actionData.z;
        }
        return "传送到了某处";
    }

    public static class EntityTravelActionData {
        int x;
        int y;
        int z;
        String cause;
    }
}