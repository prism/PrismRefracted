package network.darkhelmet.prism.utils;

import me.botsko.prism.Prism;
import me.botsko.prism.PrismLocalization;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class DeathUtils {

    private static PrismLocalization prismLocalization = null;

    /**
     * Returns the name of what caused an entity to die.
     *
     * @return String
     */
    public static String getCauseNiceName(Entity entity) {

        EntityDamageEvent e = entity.getLastDamageCause();

        if (e == null) {
            return "未知";
        }

        // Determine the root cause
        DamageCause damageCause = e.getCause();
        Entity killer = null;

        // If was damaged by an entity
        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entity
                    .getLastDamageCause();
            // Arrow?
            if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
                Projectile arrow = (Arrow) entityDamageByEntityEvent.getDamager();
                ProjectileSource source = arrow.getShooter();
                if (source instanceof Player) {
                    killer = ((Player) source);
                }
            } else {
                killer = entityDamageByEntityEvent.getDamager();
            }
        }

        if (entity instanceof Player) {

            Player player = (Player) entity;

            // Detect additional suicide. For example, when you potion
            // yourself with instant damage it doesn't show as suicide.
            if (killer instanceof Player) {
                // Themself
                if (killer.getName().equals(player.getName())) {
                    return "自杀";
                }
                // translate bukkit events to nicer names
                if ((damageCause.equals(DamageCause.ENTITY_ATTACK) || damageCause.equals(DamageCause.PROJECTILE))) {
                    return "PVP";
                }
            }
        }

        // Causes of death for either entities or players
        if (damageCause.equals(DamageCause.ENTITY_ATTACK)) {
            return "生物";
        } else if (damageCause.equals(DamageCause.PROJECTILE)) {
            return "骷髅";
        } else if (damageCause.equals(DamageCause.ENTITY_EXPLOSION)) {
            return "苦力怕";
        } else if (damageCause.equals(DamageCause.CONTACT)) {
            return "仙人掌";
        } else if (damageCause.equals(DamageCause.BLOCK_EXPLOSION)) {
            return "TNT";
        } else if (damageCause.equals(DamageCause.FIRE) || damageCause.equals(DamageCause.FIRE_TICK)) {
            return "火";
        } else if (damageCause.equals(DamageCause.MAGIC)) {
            return "魔法";
        }
        return damageCause.name().toLowerCase();
    }

    /**
     * Returns the name of the attacker, whether mob or player.
     *
     * @return String
     */
    public static String getAttackerName(Entity victim) {

        // Determine base cause
        String cause = getCauseNiceName(victim);

        if (victim instanceof Player) {
            Player killer = ((Player) victim).getKiller();
            if (killer != null) {
                return killer.getName();
            }
        }

        if (cause.equals("生物")) {

            Entity killer = ((EntityDamageByEntityEvent) Objects.requireNonNull(
                    victim.getLastDamageCause())).getDamager();
            return getEntityName(killer);
        }
        return cause;
    }

    private static String getEntityName(Entity entity) {
        if (entity instanceof Player) {
            return entity.getName();
        } else if (entity instanceof Skeleton) {
            if (entity instanceof WitherSkeleton) {
                return "凋零骷髅";
            } else {
                return "骷髅";
            }
        } else if (entity instanceof Arrow) {
            return "骷髅";
        } else if (entity instanceof Wolf) {
            Tameable wolf = (Wolf) entity;
            if (wolf.isTamed()) {
                if (wolf.getOwner() instanceof Player || wolf.getOwner() instanceof OfflinePlayer) {
                    return "PVP狼";
                } else {
                    return "狼";
                }
            } else {
                return "狼";
            }
        } else {
            return entity.getType().name().toLowerCase();
        }
    }

    /**
     * Returns the name of the attacker, whether mob or player.
     *
     * @return String.
     */
    @SuppressWarnings("unused")
    public static String getVictimName(Entity victim) {
        return getEntityName(victim);
    }

    /**
     * Determines the owner of a tamed wolf.
     *
     * @param event  EntityDeathEvent
     * @return String
     */
    public static String getTameWolfOwner(EntityDeathEvent event) {
        String owner = "";
        EntityDamageByEntityEvent e =
                ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause());
        if (e != null) {
            Entity killer = e.getDamager();
            if (killer instanceof Wolf) {
                Wolf wolf = (Wolf) killer;
                if (wolf.isTamed()) {
                    if (wolf.getOwner() instanceof Player) {
                        owner = ((Player) wolf.getOwner()).getName();
                    }
                    if (wolf.getOwner() instanceof OfflinePlayer) {
                        owner = wolf.getOwner().getName();
                    }
                }
            }
        }
        return owner;
    }

    /**
     * Determines the weapon used to kill an entity.
     *
     * @return String
     */
    @SuppressWarnings("unused")
    public static String getWeapon(LivingEntity entity) {
        String deathWeapon = "";
        if (entity.getKiller() != null) {
            ItemStack weapon = entity.getKiller().getInventory().getItemInMainHand();
            deathWeapon = prismLocalization() != null && prismLocalization().hasMaterialLocale(weapon.getType().name())?
                    prismLocalization().getMaterialLocale(weapon.getType().name())
                    : weapon.getType().toString().toLowerCase();
            deathWeapon = deathWeapon.replaceAll("_", " ");
            if (deathWeapon.equalsIgnoreCase("air")) {
                deathWeapon = " 手";
            }
        }
        return deathWeapon;
    }

    private static PrismLocalization prismLocalization() {
        if (prismLocalization == null) {
            prismLocalization = Prism.getInstance().getPrismLocalization();
        }
        return prismLocalization;
    }
}