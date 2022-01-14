package me.botsko.prism.actions;

import me.botsko.prism.api.ChangeResult;
import me.botsko.prism.api.ChangeResultType;
import me.botsko.prism.api.PrismParameters;
import me.botsko.prism.appliers.ChangeResultImpl;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;

public class VehicleAction extends GenericAction {
    private String vehicleName;

    /**
     * Set the vehicle.
     * @param vehicle Entity
     */
    public void setVehicle(Entity vehicle) {

        if (vehicle instanceof CommandMinecart) {
            vehicleName = "命令方块矿车";
        } else if (vehicle instanceof ExplosiveMinecart) {
            vehicleName = "TNT矿车";
        } else if (vehicle instanceof HopperMinecart) {
            vehicleName = "漏斗矿车";
        } else if (vehicle instanceof PoweredMinecart) {
            vehicleName = "动力矿车";
        } else if (vehicle instanceof RideableMinecart) {
            vehicleName = "矿车";
        } else if (vehicle instanceof SpawnerMinecart) {
            vehicleName = "刷怪笼矿车";
        } else if (vehicle instanceof StorageMinecart) {
            vehicleName = "运输矿车";
        } else if (vehicle instanceof Boat) {
            vehicleName = "船";
        } else {
            vehicleName = vehicle.getType().name().toLowerCase();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNiceName() {
        return vehicleName;
    }

    @Override
    public boolean hasExtraData() {
        return vehicleName != null;
    }

    @Override
    public String serialize() {
        return vehicleName;
    }

    @Override
    public void deserialize(String data) {
        vehicleName = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChangeResult applyRollback(Player player, PrismParameters parameters, boolean isPreview) {
        Entity vehicle = null;
        switch (vehicleName) {
            case "命令方块矿车":
                vehicle = getWorld().spawn(getLoc(), CommandMinecart.class);
                break;
            case "动力矿车":
                vehicle = getWorld().spawn(getLoc(), PoweredMinecart.class);
                break;
            case "运输矿车":
                vehicle = getWorld().spawn(getLoc(), StorageMinecart.class);
                break;
            case "TNT矿车":
                vehicle = getWorld().spawn(getLoc(), ExplosiveMinecart.class);
                break;
            case "刷怪笼矿车":
                vehicle = getWorld().spawn(getLoc(), SpawnerMinecart.class);
                break;
            case "漏斗矿车":
                vehicle = getWorld().spawn(getLoc(), HopperMinecart.class);
                break;
            case "矿车":
                vehicle = getWorld().spawn(getLoc(), Minecart.class);
                break;
            case "船":
                vehicle = getWorld().spawn(getLoc(), Boat.class);
                break;
            default:
                //null
        }
        if (vehicle != null) {
            return new ChangeResultImpl(ChangeResultType.APPLIED, null);
        }
        return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
    }
}
