package me.botsko.prism.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantmentUtils {

    /**
     * Given an enchantment, does the current item have any that conflict.
     *
     * @return bool
     */
    @SuppressWarnings("unused")
    public static boolean hasConflictingEnchanment(ItemStack item, Enchantment ench) {
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        boolean conflict = false;
        for (Enchantment e : enchantments.keySet()) {
            if (ench.conflictsWith(e)) {
                conflict = true;
            }
        }
        return (enchantments.containsKey(ench) || conflict);
    }

    /**
     * Return the enchantment based on a common name.
     *
     * @param name String
     * @return Enchantment
     */
    @SuppressWarnings("unused")
    public static Enchantment getEnchantmentFromCommonName(String name) {
        switch (name.toLowerCase()) {
            case "aquaaffinity":
                return Enchantment.WATER_WORKER;
            case "bane":
                return Enchantment.DAMAGE_ARTHROPODS;
            case "efficiency":
                return Enchantment.DIG_SPEED;
            case "explosion":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "fall":
                return Enchantment.PROTECTION_FALL;
            case "fire":
                return Enchantment.PROTECTION_FIRE;
            case "fireaspect":
                return Enchantment.FIRE_ASPECT;
            case "flame":
                return Enchantment.ARROW_FIRE;
            case "fortune":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "infinity":
                return Enchantment.ARROW_INFINITE;
            case "knockback":
                return Enchantment.KNOCKBACK;
            case "power":
                return Enchantment.ARROW_DAMAGE;
            case "looting":
                return Enchantment.LOOT_BONUS_MOBS;
            case "projectile":
                return Enchantment.PROTECTION_PROJECTILE;
            case "protection":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "punch":
                return Enchantment.ARROW_KNOCKBACK;
            case "respiration":
                return Enchantment.OXYGEN;
            case "sharpness":
                return Enchantment.DAMAGE_ALL;
            case "silktouch":
                return Enchantment.SILK_TOUCH;
            case "smite":
                return Enchantment.DAMAGE_UNDEAD;
            case "unbreaking":
                return Enchantment.DURABILITY;
            default:
                String formattedName = name.replace(' ','_');
                NamespacedKey key = NamespacedKey.minecraft(formattedName);
                return Enchantment.getByKey(key);
        }
    }

    /**
     * Return the common name for an enchantment.
     *
     * @param enchantment Keyed
     * @param level int
     * @return String
     */
    public static String getClientSideEnchantmentName(Enchantment enchantment, int level) {

        String enchantName;

        if (enchantment.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {
            enchantName = "保护";
        } else if (enchantment.equals(Enchantment.PROTECTION_FIRE)) {
            enchantName = "火焰保护";
        } else if (enchantment.equals(Enchantment.PROTECTION_FALL)) {
            enchantName = "摔落保护";
        } else if (enchantment.equals(Enchantment.PROTECTION_EXPLOSIONS)) {
            enchantName = "爆炸保护";
        } else if (enchantment.equals(Enchantment.PROTECTION_PROJECTILE)) {
            enchantName = "弹射物保护";
        } else if (enchantment.equals(Enchantment.OXYGEN)) {
            enchantName = "水下呼吸";
        } else if (enchantment.equals(Enchantment.WATER_WORKER)) {
            enchantName = "水下速掘";
        } else if (enchantment.equals(Enchantment.THORNS)) {
            enchantName = "荆棘";
        } else if (enchantment.equals(Enchantment.DEPTH_STRIDER)) {
            enchantName = "深海探索者";
        } else if (enchantment.equals(Enchantment.FROST_WALKER)) {
            enchantName = "冰霜行者";
        } else if (enchantment.equals(Enchantment.BINDING_CURSE)) {
            enchantName = "绑定诅咒";
        } else if (enchantment.equals(Enchantment.DAMAGE_ALL)) {
            enchantName = "锋利";
        } else if (enchantment.equals(Enchantment.DAMAGE_UNDEAD)) {
            enchantName = "亡灵杀手";
        } else if (enchantment.equals(Enchantment.DAMAGE_ARTHROPODS)) {
            enchantName = "节肢杀手";
        } else if (enchantment.equals(Enchantment.KNOCKBACK)) {
            enchantName = "击退";
        } else if (enchantment.equals(Enchantment.FIRE_ASPECT)) {
            enchantName = "火焰附加";
        } else if (enchantment.equals(Enchantment.LOOT_BONUS_MOBS)) {
            enchantName = "掠夺";
        } else if (enchantment.equals(Enchantment.SWEEPING_EDGE)) {
            enchantName = "横扫之刃";
        } else if (enchantment.equals(Enchantment.DIG_SPEED)) {
            enchantName = "效率";
        } else if (enchantment.equals(Enchantment.SILK_TOUCH)) {
            enchantName = "精准采集";
        } else if (enchantment.equals(Enchantment.DURABILITY)) {
            enchantName = "耐久";
        } else if (enchantment.equals(Enchantment.LOOT_BONUS_BLOCKS)) {
            enchantName = "时运";
        } else if (enchantment.equals(Enchantment.ARROW_DAMAGE)) {
            enchantName = "力量";
        } else if (enchantment.equals(Enchantment.ARROW_KNOCKBACK)) {
            enchantName = "冲击";
        } else if (enchantment.equals(Enchantment.ARROW_FIRE)) {
            enchantName = "火矢";
        } else if (enchantment.equals(Enchantment.ARROW_INFINITE)) {
            enchantName = "无限";
        } else if (enchantment.equals(Enchantment.LUCK)) {
            enchantName = "海之眷顾";
        } else if (enchantment.equals(Enchantment.LURE)) {
            enchantName = "饵钓";
        } else if (enchantment.equals(Enchantment.LOYALTY)) {
            enchantName = "忠诚";
        } else if (enchantment.equals(Enchantment.IMPALING)) {
            enchantName = "穿刺";
        } else if (enchantment.equals(Enchantment.RIPTIDE)) {
            enchantName = "激流";
        } else if (enchantment.equals(Enchantment.CHANNELING)) {
            enchantName = "引雷";
        } else if (enchantment.equals(Enchantment.MULTISHOT)) {
            enchantName = "多重射击";
        } else if (enchantment.equals(Enchantment.QUICK_CHARGE)) {
            enchantName = "快速装填";
        } else if (enchantment.equals(Enchantment.PIERCING)) {
            enchantName = "穿透";
        } else if (enchantment.equals(Enchantment.MENDING)) {
            enchantName = "经验修补";
        } else if (enchantment.equals(Enchantment.VANISHING_CURSE)) {
            enchantName = "消失诅咒";
        } else if (enchantment.equals(Enchantment.SOUL_SPEED)) {
            enchantName = "灵魂疾行";
        } else {
            enchantName = enchantment.getKey().getKey().toLowerCase().replace("_", " ");
        }
        switch (level) {
            case 1:
                enchantName += " I";
                break;
            case 2:
                enchantName += " II";
                break;
            case 3:
                enchantName += " III";
                break;
            case 4:
                enchantName += " IV";
                break;
            case 5:
                enchantName += " V";
                break;
            default:
                enchantName += " " + level;
        }
        return enchantName;

    }
}
