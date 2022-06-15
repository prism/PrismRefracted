package io.github.rothes.prismcn;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.darkhelmet.prism.Prism;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CNLocalization {

    private static final HashMap<EntityType, String> entityLocalize = new HashMap<>();
    private static final HashMap<Material, String> materialLocalize = new HashMap<>();
    private static final HashMap<PotionEffectType, String> effectLocalize = new HashMap<>();
    private static final HashMap<Enchantment, String> enchantmentLocalize = new HashMap<>();

    private static final HashMap<String, String> entityLocalizeRestore = new HashMap<>();

    public static void initialize(Prism plugin) {
        JsonElement root;
        try {
            InputStream stream = plugin.getResource("languages/Minecraft-Lang.json");
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            root = new JsonParser().parse(reader);
            reader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Prism.warn("无法加载本地化语言文件");
            return;
        }
        JsonObject object = root.getAsJsonObject();
        for (EntityType value : EntityType.values()) {
            if (value == EntityType.UNKNOWN) {
                entityLocalize.put(value, "未知");
                continue;
            }
            JsonElement element = object.get("entity.minecraft." + value.getKey().getKey());
            if (element == null) {
                Prism.warn("缺少本地化语言: EntityType = " + value.name());
                entityLocalize.put(value, value.name().toLowerCase().replace("_", " "));
                entityLocalizeRestore.put(value.name().toLowerCase().replace("_", " "), value.name());
            } else {
                entityLocalize.put(value, element.getAsString());
                entityLocalizeRestore.put(element.getAsString(), value.name());
            }
        }

        for (Material value : Material.values()) {
            switch (value) {
                case WHITE_WALL_BANNER:
                    materialLocalize.put(value, "白色旗帜");
                    break;
                case ORANGE_WALL_BANNER:
                    materialLocalize.put(value, "橙色旗帜");
                    break;
                case MAGENTA_WALL_BANNER:
                    materialLocalize.put(value, "品红色旗帜");
                    break;
                case LIGHT_BLUE_WALL_BANNER:
                    materialLocalize.put(value, "淡蓝色旗帜");
                    break;
                case YELLOW_WALL_BANNER:
                    materialLocalize.put(value, "黄色旗帜");
                    break;
                case LIME_WALL_BANNER:
                    materialLocalize.put(value, "黄绿色旗帜");
                    break;
                case PINK_WALL_BANNER:
                    materialLocalize.put(value, "粉红色旗帜");
                    break;
                case GRAY_WALL_BANNER:
                    materialLocalize.put(value, "灰色旗帜");
                    break;
                case LIGHT_GRAY_WALL_BANNER:
                    materialLocalize.put(value, "淡灰色旗帜");
                    break;
                case CYAN_WALL_BANNER:
                    materialLocalize.put(value, "青色旗帜");
                    break;
                case PURPLE_WALL_BANNER:
                    materialLocalize.put(value, "紫色旗帜");
                    break;
                case BLUE_WALL_BANNER:
                    materialLocalize.put(value, "蓝色旗帜");
                    break;
                case BROWN_WALL_BANNER:
                    materialLocalize.put(value, "棕色旗帜");
                    break;
                case GREEN_WALL_BANNER:
                    materialLocalize.put(value, "绿色旗帜");
                    break;
                case RED_WALL_BANNER:
                    materialLocalize.put(value, "红色旗帜");
                    break;
                case BLACK_WALL_BANNER:
                    materialLocalize.put(value, "黑色旗帜");
                    break;
                default:
                    JsonElement element = object.get("item.minecraft." + value.getKey().getKey());
                    if (element == null) {
                        element = object.get("block.minecraft." + value.getKey().getKey());
                    }
                    if (element == null) {
                        Prism.warn("缺少本地化语言: Material = " + value.name());
                        materialLocalize.put(value, value.name().toLowerCase().replace("_", " "));
                    } else {
                        materialLocalize.put(value, element.getAsString());
                    }
                    break;
            }
        }
        for (PotionEffectType value : PotionEffectType.values()) {
            JsonElement element = object.get("effect.minecraft." + value.getKey().getKey());
            if (element == null) {
                Prism.warn("缺少本地化语言: PotionEffectType = " + value.getKey().getKey());
                effectLocalize.put(value, value.getKey().getKey().toLowerCase().replace("_", " "));
            } else {
                effectLocalize.put(value, element.getAsString());
            }
        }
        for (Enchantment value : Enchantment.values()) {
            JsonElement element = object.get("enchantment.minecraft." + value.getKey().getKey());
            if (element == null) {
                Prism.warn("缺少本地化语言: Enchantment = " + value.getKey().getKey());
                enchantmentLocalize.put(value, value.getKey().getKey().toLowerCase().replace("_", " "));
            } else {
                enchantmentLocalize.put(value, element.getAsString());
            }
        }


    }

    public static String getMaterialLocale(Material material) {
        return materialLocalize.get(material);
    }

    public static String getEntityLocale(EntityType entityType) {
        return entityLocalize.get(entityType);
    }

    public static String getEntityLocale(String entityType) {
        return entityLocalize.get(EntityType.valueOf(entityType));
    }

    public static String getEffectLocale(PotionEffectType potionEffectType) {
        return effectLocalize.get(potionEffectType);
    }

    public static String getEnchantmentLocale(Enchantment enchantment) {
        return enchantmentLocalize.get(enchantment);
    }

    public static String restoreEntityLocale(String type) {
        return entityLocalizeRestore.getOrDefault(type, type);
    }

}
