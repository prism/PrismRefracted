package network.darkhelmet.prism.database;

import org.bukkit.entity.Player;

public abstract class AbstractSettingsQuery implements SettingsQuery {
    /**
     * Get the Player key name.
     * @param player the Player
     * @param key    the setting to return
     * @return String
     */
    public String getPlayerKey(Player player, String key) {
        return player.getName() + "." + key;
    }
}
