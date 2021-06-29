package me.botsko.prism.database;

import org.bukkit.entity.Player;

public interface SettingsQuery {
    void deleteSetting(String key, Player player);

    void saveSetting(String key, String value, Player player);

    String getSetting(String key, Player player);
}
