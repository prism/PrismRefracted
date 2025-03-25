package network.darkhelmet.prism.utils;

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
     * Return the common name for an enchantment.
     *
     * @param enchantment Keyed
     * @param level int
     * @return String
     */
    public static String getClientSideEnchantmentName(Enchantment enchantment, int level) {
        String enchantName = enchantment.getKey().getKey().toLowerCase().replace("_", " ");

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
