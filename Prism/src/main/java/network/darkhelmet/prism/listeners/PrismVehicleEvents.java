package network.darkhelmet.prism.listeners;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.ActionFactory;
import network.darkhelmet.prism.actionlibs.RecordingQueue;
import io.github.rothes.prismcn.PrismLocalization;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PrismVehicleEvents implements Listener {

    private final Prism plugin;
    private final PrismLocalization prismLocalization;

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public PrismVehicleEvents(Prism plugin) {
        this.plugin = plugin;
        prismLocalization = plugin.getPrismLocalization();
    }

    /**
     * VehicleCreateEvent.
     *
     * @param event VehicleCreateEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleCreate(final VehicleCreateEvent event) {

        final Vehicle vehicle = event.getVehicle();
        final Location loc = vehicle.getLocation();

        final String coord_key = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        String value = plugin.preplannedVehiclePlacement.get(coord_key);
        if (value == null) {
            // Not direct put needs y + 1
            final String coord_key_1 = loc.getBlockX() + ":" + (loc.getBlockY() + 1) + ":" + loc.getBlockZ();
            value = plugin.preplannedVehiclePlacement.get(coord_key_1);
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(value);
        } catch (Exception ignored) {
            //ignored.
        }
        final OfflinePlayer player = uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
        if (player != null) {
            // TODO: name ref
            if (!Prism.getIgnore().event("vehicle-place", loc.getWorld(), player.getName())) {
                return;
            }
            RecordingQueue.addToQueue(ActionFactory.createVehicle("vehicle-place", vehicle, player));

        } else {
            if (!Prism.getIgnore().event("vehicle-place", loc.getWorld(), "未知")) {
                return;
            }
            RecordingQueue.addToQueue(ActionFactory.createVehicle("vehicle-place", vehicle, "未知"));
        }
    }

    /**
     * VehicleDestroyEvent.
     *
     * @param event VehicleDestroyEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleDestroy(final VehicleDestroyEvent event) {

        final Vehicle vehicle = event.getVehicle();
        final Entity attacker = event.getAttacker();
        // Was it broken by an attack
        if (attacker != null) {
            handlePlayerAction(attacker, vehicle, "vehicle-break");
        } else {

            // Otherwise its driver was reckless
            final List<Entity> passengers = vehicle.getPassengers();
            if (!passengers.isEmpty()) {
                Entity passenger = passengers.get(0);
                handlePlayerAction(passenger, vehicle, "vehicle-break");
            }
        }

        if (vehicle instanceof ChestBoat) {
            ChestBoat chestBoat = (ChestBoat) vehicle;
            for (final ItemStack item : chestBoat.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    String woodType;
                    switch (chestBoat.getWoodType()) {
                        case GENERIC:
                            woodType = "橡木";
                            break;
                        case REDWOOD:
                            woodType = "红树木";
                            break;
                        case BIRCH:
                            woodType = "白桦木";
                            break;
                        case JUNGLE:
                            woodType = "从林木";
                            break;
                        case ACACIA:
                            woodType = "金合欢木";
                            break;
                        case DARK_OAK:
                            woodType = "深色橡木";
                            break;
                        default:
                            woodType = chestBoat.getWoodType().name().toLowerCase() + " ";
                            break;
                    }
                    RecordingQueue.addToQueue(ActionFactory.createItemStack("item-drop", item,
                            item.getAmount(), -1, null, vehicle.getLocation(),
                            woodType + "运输船"));
                }
            }

        }
    }

    /**
     * VehicleEnterEvent.
     *
     * @param event VehicleEnterEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        final Vehicle vehicle = event.getVehicle();
        final Entity entity = event.getEntered();
        handlePlayerAction(entity, vehicle, "vehicle-enter");
    }

    /**
     * VehicleExitEvent.
     *
     * @param event VehicleExitEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleExit(final VehicleExitEvent event) {

        final Vehicle vehicle = event.getVehicle();
        final Entity entity = event.getExited();
        handlePlayerAction(entity, vehicle, "vehicle-exit", "vehicle-enter");
    }

    private void handlePlayerAction(Entity entity, Vehicle vehicle, String action) {
        handlePlayerAction(entity, vehicle, action, action);
    }

    private void handlePlayerAction(Entity entity, Vehicle vehicle, String action, String customCheck) {
        if (entity instanceof Player) {
            if (!Prism.getIgnore().event(customCheck, ((Player) entity))) {
                return;
            }
            RecordingQueue.addToQueue(ActionFactory.createVehicle(action, vehicle, (Player) entity));
        } else {
            if (!Prism.getIgnore().event(customCheck, entity.getWorld())) {
                return;
            }
            RecordingQueue.addToQueue(ActionFactory.createVehicle(action, vehicle,
                    prismLocalization.hasEntityLocale(entity.getType().name()) ?
                            prismLocalization.getEntityLocale(entity.getType().name()) : entity.getType().name().toLowerCase()));
        }
    }
}