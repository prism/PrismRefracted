package network.darkhelmet.prism.utils;

import network.darkhelmet.prism.Prism;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NmsUtils {

    private static final EnumMap<InventoryType, WrappedSlot[]> itemInventoryAccepts = new EnumMap<>(InventoryType.class);

    private NmsUtils() {
        // private
    }

    /**
     * @param inventoryView
     * @param itemStack
     * @param index
     * @return If the inventory accepts the itemstack added by hoppers of players by shift-click.
     */
    public static boolean canAcceptPlaceQuick(InventoryView inventoryView, ItemStack itemStack, int index) {
        if (!itemInventoryAccepts.containsKey(inventoryView.getType())) {
            itemInventoryAccepts.put(inventoryView.getType(), cacheInventoryAccepts(inventoryView));
        }
        WrappedSlot[] slots = itemInventoryAccepts.get(inventoryView.getType());
        if (slots == null) {
            // Failed when cached, don't check.
            return true;
        }
        return slots[index].acceptsPlace(itemStack);
    }

    private static WrappedSlot[] cacheInventoryAccepts(InventoryView inventoryView) {
        Object containerMenu = getNmsHandle(inventoryView);
        if (containerMenu == null) {
            // Failed
            return null;
        }
        List<Object> slots;
        try {
            Class<?> abstractContainer = containerMenu.getClass();
            while (true) {
                Class<?> temp = abstractContainer.getSuperclass();
                if (temp == Object.class) {
                    break;
                }
                abstractContainer = temp;
            }
            Field fieldSlots = Arrays.stream(abstractContainer.getDeclaredFields())
                    .filter(it -> AbstractList.class.isAssignableFrom(it.getType())).collect(Collectors.toList()).get(1);
            fieldSlots.setAccessible(true);
            //noinspection unchecked
            slots = (List<Object>) fieldSlots.get(containerMenu);
        } catch (IllegalAccessException e) {
            Prism.getInstance().getLogger().log(Level.WARNING, "Cannot get NMS container menu slots.", e);
            return null;
        }
        Object nmsItemStack = getNmsHandle(new ItemStack(Material.AIR));
        if (nmsItemStack == null) {
            return null;
        }
        List<WrappedSlot> res = new ArrayList<>();
        try {
            for (Object slot : slots) {
                Class<?> base = slot.getClass();
                while (true) {
                    Class<?> temp = base.getSuperclass();
                    if (temp == Object.class) {
                        break;
                    }
                    base = temp;
                }

                Field fieldSlotIndex = Arrays.stream(base.getDeclaredFields())
                        .filter(it -> it.getType() == int.class).findFirst().get();
                fieldSlotIndex.setAccessible(true);
                int slotIndex = (int) fieldSlotIndex.get(slot);

                Class<?> canPlaceClass = slot.getClass();
                Optional<Method> optional;
                while (!(optional = Arrays.stream(canPlaceClass.getDeclaredMethods())
                        .filter(it -> it.getReturnType() == boolean.class && it.getParameterCount() == 1
                                && it.getParameterTypes()[0] == nmsItemStack.getClass()).findFirst()).isPresent()) {
                    // The method may exist in super class.
                    canPlaceClass = canPlaceClass.getSuperclass();
                    if (canPlaceClass == Object.class) {
                        // Break, and throw NoSuchElementException later.
                        break;
                    }
                }
                Method method = optional.get();
                method.setAccessible(true);
                res.add(new WrappedSlot(slotIndex, method, slot));
            }
        } catch (NoSuchElementException | IllegalAccessException e) {
            Prism.getInstance().getLogger().log(Level.WARNING, "Cannot wrap NMS Slot handles.", e);
            return null;
        }
        return res.toArray(new WrappedSlot[0]);
    }

    private static Object getNmsHandle(InventoryView inventoryView) {
        Object container;
        try {
            Method getNms = inventoryView.getClass().getDeclaredMethod("getHandle");
            getNms.setAccessible(true);
            container = getNms.invoke(inventoryView); // NMS Container instance
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Prism.getInstance().getLogger().log(Level.WARNING, "Cannot get NMS container menu from Bukkit inventoryView.", e);
            return null;
        }
        return container;
    }

    private static Object getNmsHandle(ItemStack itemStack) {
        try {
            Class<?> craftClass = getCraftItemStack();
            if (craftClass == null) {
                return null;
            }
            if (craftClass.isInstance(itemStack)) {
                Field handle = craftClass.getDeclaredField("handle");
                handle.setAccessible(true);
                return handle.get(itemStack);
            } else {
                Method asNMSCopy = craftClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
                asNMSCopy.setAccessible(true);
                return asNMSCopy.invoke(null, itemStack);
            }
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            Prism.getInstance().getLogger().log(Level.WARNING, "Cannot get NMS itemstack from Bukkit itemstack.", e);
            return null;
        }
    }

    private static Class<?> getCraftItemStack() {
        String name = Bukkit.getServer().getClass().getCanonicalName();
        try {
            return Class.forName(name.replace(".CraftServer", ".inventory.CraftItemStack"));
        } catch (ClassNotFoundException e) {
            Prism.getInstance().getLogger().log(Level.WARNING, "Cannot get CraftItemStack class.", e);
            return null;
        }
    }

    public static class WrappedSlot {

        /**
         * Slot index
         */
        private final int slot;
        /**
         * public boolean mayPlace(net.minecraft.world.item.ItemStack var0)
         */
        private final Method acceptsItemPlace;
        /**
         * NMS Slot handle
         */
        private final Object handle;

        public WrappedSlot(int slot, Method acceptsItemPlace, Object handle) {
            this.slot = slot;
            this.acceptsItemPlace = acceptsItemPlace;
            this.handle = handle;
        }

        public int slot() {
            return slot;
        }

        public boolean acceptsPlace(ItemStack itemStack) {
            Object nms = getNmsHandle(itemStack);
            if (nms == null) {
                // Failed to get NMS handle, don't check.
                return true;
            }
            try {
                return (boolean) acceptsItemPlace.invoke(this.handle, nms);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
