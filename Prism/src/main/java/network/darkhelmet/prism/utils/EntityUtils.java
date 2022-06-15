package network.darkhelmet.prism.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeSpecies;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityUtils {

    private static final HashMap<String, String> descriptionCache = new HashMap<>();

    /**
     * Get Offline player. Should run Async.
     * @param uuidOrName String
     * @return OfflinePlayer
     */
    public static OfflinePlayer offlineOf(String uuidOrName) {
        if (uuidOrName != null) {
            OfflinePlayer result;
            try {
                result = Bukkit.getOfflinePlayer(UUID.fromString(uuidOrName));
            } catch (IllegalArgumentException e) {
                @SuppressWarnings("deprecation")
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuidOrName);
                result = player;
            }

            return result.hasPlayedBefore() ? result : null;
        }

        return null;
    }

    /**
     * Get Uuid - should be handled Async as this may do a remote call.
     * @param uuidOrName String
     * @return Uuid.
     */
    public static UUID uuidOf(String uuidOrName) {
        if (uuidOrName != null) {
            try {
                return UUID.fromString(uuidOrName);
            } catch (IllegalArgumentException ignored) {
                //ignored
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuidOrName);
            return player.getUniqueId();
        }

        return null;
    }

    /**
     * Projectile Description.
     * @param source Projectile
     * @return String.
     */
    public static String getCustomProjectileDescription(Projectile source) {
        String description = descriptionCache.get(source.getClass().getSimpleName());

        if (description == null) {
            //TODO: Rothes - 校对翻译
            if (source instanceof org.bukkit.entity.Trident) {
                description = "用三叉戟插了";
            } else if (source instanceof org.bukkit.entity.Arrow) {
                description = "用箭射了";
            } else if (source instanceof org.bukkit.entity.Egg) {
                description = "用蛋砸了";
            } else if (source instanceof org.bukkit.entity.EnderPearl) {
                description = "用末影珍珠砸了";
            } else if (source instanceof org.bukkit.entity.SmallFireball) {
                description = "用小火球点燃了";
            } else if (source instanceof org.bukkit.entity.Fireball) {
                description = "用火球炸了";
            } else if (source instanceof org.bukkit.entity.FishHook) {
                description = "用钓竿钩了";
            } else if (source instanceof org.bukkit.entity.ThrownPotion) {
                description = "用药水喷了";
            } else if (source instanceof org.bukkit.entity.LlamaSpit) {
                description = "吐唾沫给了";
            } else if (source instanceof org.bukkit.entity.ShulkerBullet) {
                description = "追到了";
            } else if (source instanceof org.bukkit.entity.Snowball) {
                description = "用雪球砸了";
            } else if (source instanceof org.bukkit.entity.ThrownExpBottle) {
                description = "用附魔瓶砸了";
            } else {
                description = "";
            }

            descriptionCache.put(source.getClass().getSimpleName(), description);
        }

        if (description.length() > 0) {
            return description;
        }

        return null;
    }

    public static void sendBlockChange(Player p, Location loc, BlockData data) {
        p.sendBlockChange(loc, data);
    }

    /**
     * Removes item drops near an entity.
     *
     * @param player Player
     * @param radius int
     * @return int number removed.
     */
    public static int removeNearbyItemDrops(Player player, int radius) {
        int removed = 0;
        List<Entity> nearby = player.getNearbyEntities(radius, radius, radius);
        for (Entity e : nearby) {
            if (e instanceof Item || e instanceof ExperienceOrb) {
                e.remove();
                removed++;
            }
        }
        return removed;
    }

    /**
     * Whether or not an entity is within a cube radius.
     *
     * @param loc1 Location
     * @param radius int
     * @param loc2 Location
     * @return in Cube.
     */
    public static boolean inCube(Location loc1, int radius, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }
        return (loc1.getBlockX() + radius > loc2.getBlockX() && loc1.getBlockX() - radius < loc2.getBlockX()
                && loc1.getBlockY() + radius > loc2.getBlockY() && loc1.getBlockY() - radius < loc2.getBlockY()
                && loc1.getBlockZ() + radius > loc2.getBlockZ() && loc1.getBlockZ() - radius < loc2.getBlockZ());
    }

    /**
     * Determines which blocks a player my "co-exist" with.
     *
     * @param m Material
     * @return boolean
     * @todo doesn't bukkit have this already?
     */

    @SuppressWarnings("deprecation")
    public static boolean playerMayPassThrough(Material m) {
        // Close enough, pray you don't land in a portal
        return m.isTransparent();
    }

    public static String treeSpeciesToName(TreeSpecies ts) {
        switch (ts) {
            case GENERIC:
                return "橡木";
            case REDWOOD:
                return "云杉木";
            case BIRCH:
                return "白桦木";
            case JUNGLE:
                return "从林木";
            case ACACIA:
                return "金合欢木";
            case DARK_OAK:
                return "深色橡木";
            default:
                return ts.name().toLowerCase().replace("_", " ");
        }
    }

    public static TreeSpecies nameToTreeSpecies(String ts) {
        switch (ts) {
            case "oak":
            case "橡木":
                return TreeSpecies.GENERIC;
            case "spruce":
            case "云杉木":
                return TreeSpecies.REDWOOD;
            case "birch":
            case "白桦木":
                return TreeSpecies.BIRCH;
            case "jungle":
            case "从林木":
                return TreeSpecies.JUNGLE;
            case "acacia":
            case "金合欢木":
                return TreeSpecies.ACACIA;
            case "dark oak":
            case "深色橡木":
                return TreeSpecies.DARK_OAK;
            default:
                return MiscUtils.getEnum(ts.toUpperCase().replace(" ", "_"), TreeSpecies.GENERIC);
        }
    }
}