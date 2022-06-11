package io.github.rothes.prismcn;

import network.darkhelmet.prism.Prism;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class PrismLocalization {

    private HashMap<String, String> entityLocalize = new HashMap<>();
    private HashMap<String, String> materialLocalize = new HashMap<>();
    private HashMap<String, String> effectLocalize = new HashMap<>();

    private HashMap<String, String> entityLocalizeRestore = new HashMap<>();

    public void initialize(Prism plugin) {
        YamlConfiguration translation = new YamlConfiguration();
        InputStream translationStream = plugin.getResource("languages/localisation.yml");
        if (translationStream != null) {
            try {
                translation.load(new InputStreamReader(translationStream, StandardCharsets.UTF_8));
            } catch (IOException | InvalidConfigurationException exception) {
                exception.printStackTrace();
                return;
            }
            ConfigurationSection section;
            section = translation.getConfigurationSection("Material");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    materialLocalize.put(key, section.getString(key));
                }
            }

            section = translation.getConfigurationSection("Entity");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    entityLocalize.put(key, section.getString(key));
                    entityLocalizeRestore.put(section.getString(key), key);
                }
            }

            section = translation.getConfigurationSection("Effect");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    effectLocalize.put(key, section.getString(key));
                }
            }

        }
    }

    public boolean hasMaterialLocale(String type) {
        return materialLocalize.containsKey(type);
    }

    public String getMaterialLocale(String type) {
        return materialLocalize.getOrDefault(type, type.toLowerCase(Locale.ROOT).replace("_", " "));
    }

    public boolean hasEntityLocale(String type) {
        return entityLocalize.containsKey(type);
    }

    public String getEntityLocale(String type) {
        return entityLocalize.getOrDefault(type, type.toLowerCase(Locale.ROOT).replace("_", " "));
    }

    public String restoreEntityLocale(String type) {
        return entityLocalizeRestore.getOrDefault(type, type);
    }

    public boolean hasEffectLocale(String type) {
        return effectLocalize.containsKey(type);
    }

    public String getEffectLocale(String type) {
        return effectLocalize.get(type);
    }

}
