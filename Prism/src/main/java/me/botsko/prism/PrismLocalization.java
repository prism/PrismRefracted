package me.botsko.prism;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PrismLocalization {

    private HashMap<String, String> entityLocalize = new HashMap<>();
    private HashMap<String, String> materialLocalize = new HashMap<>();
    private HashMap<String, String> effectLocalize = new HashMap<>();

    public void initialize(Prism plugin) {
        YamlConfiguration translation = new YamlConfiguration();
        InputStream translationStream = plugin.getResource("languages/localisation.yml");
        if (translationStream != null) {
            try {
                translation.load(new InputStreamReader(translationStream));
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
        return materialLocalize.get(type);
    }

    public boolean hasEntityLocale(String type) {
        return entityLocalize.containsKey(type);
    }

    public String getEntityLocale(String type) {
        return entityLocalize.get(type);
    }

    public boolean hasEffectLocale(String type) {
        return effectLocalize.containsKey(type);
    }

    public String getEffectLocale(String type) {
        return effectLocalize.get(type);
    }

}
