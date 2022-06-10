package network.darkhelmet.prism.players;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.database.sql.SqlPlayerIdentificationHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerIdentification {

    /**
     * Loads `prism_players` ID for a real player into our cache.
     * Runs during PlayerJoin events, so it will never be for a fake/null player.
     *
     * @param uuid Player uuid
     * @param name String
     */
    public static void cachePrismPlayer(UUID uuid, String name) {
        PrismPlayer prismPlayer;
        prismPlayer = getPrismPlayer(uuid, name);
        if (prismPlayer != null) {
            comparePlayerToCache(name, uuid, prismPlayer);
            Prism.debug("已加载玩家 " + name + ", ID: " + uuid + " 到缓存.");
            Prism.prismPlayers.put(uuid, prismPlayer);
            return;
        }
        SqlPlayerIdentificationHelper.addPlayer(name, uuid);
    }

    /**
     * Gets a player from the cache by name in general this is always an online player but if not it
     * will attempt to get the player id from the database.
     */
    public static PrismPlayer getPrismPlayerByNameFromCache(final String playerName) {

        // Lookup the player
        PrismPlayer prismPlayer = getPrismPlayer(playerName);
        if (prismPlayer != null) {
            // prismPlayer = comparePlayerToCache( player, prismPlayer );
            // Prism.debug("已加载玩家 " + prismPlayer.getName() + ", ID: " + prismPlayer.getId() + " 到缓存.");
            // Prism.prismPlayers.put( player.getUniqueId(), prismPlayer );
            return prismPlayer;
        }

        // Player is fake, create a record for them
        prismPlayer = SqlPlayerIdentificationHelper.addFakePlayer(playerName);

        return prismPlayer;

    }

    /**
     * Returns a `prism_players` ID for the described player name. If one cannot be
     * found, returns 0.
     * Used by the recorder in determining proper foreign key
     * - Possibly performs db lookup.,
     *
     * @param playerName String
     * @return PrismPlayer
     */
    private static PrismPlayer getPrismPlayer(String playerName) {

        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            return getPrismPlayer(player.getUniqueId(), player.getName());
        }

        // Player not online, we need to go to cache

        return SqlPlayerIdentificationHelper.lookupByName(playerName);

    }

    /**
     * Returns a `prism_players` ID for the described player object. If one cannot
     * be found, returns 0. - Possibly performs db lookup.,
     * Used by the recorder in determining proper foreign key
     *
     * @return PrismPlayer
     */
    private static @Nullable PrismPlayer getPrismPlayer(UUID uuid, String name) {

        PrismPlayer prismPlayer;
        // Are they in the cache?
        prismPlayer = Prism.prismPlayers.get(uuid);
        if (prismPlayer != null) {
            return prismPlayer;
        }

        // Lookup by UUID
        prismPlayer = SqlPlayerIdentificationHelper.lookupByUuid(uuid);
        if (prismPlayer != null) {
            if (!prismPlayer.getName().equals(name)) {
                prismPlayer.setName(name);
                SqlPlayerIdentificationHelper.updatePlayer(prismPlayer);
            }
            return prismPlayer;
        }
        // Still not found, try looking them up by name
        prismPlayer = SqlPlayerIdentificationHelper.lookupByName(name);
        prismPlayer = comparePlayerToCache(name, uuid, prismPlayer);
        // now check if the uuid is the same as the one logging in ...if it isn't we likely need to
        // create a new player and update the old one with a new name
        return prismPlayer;

    }

    /**
     * Compares the known player to the cached data. If there's a difference we need
     * to handle it.
     * If usernames are different: Update `prism_players` with new name (@todo track
     * historical?)
     * If UUID is different, log an error.
     *
     * @param name        Player name
     * @param uuid        UUID player uuid
     * @param prismPlayer PrismPlayer
     * @return PrismPlayer
     */

    private static @Nullable PrismPlayer comparePlayerToCache(final String name, final UUID uuid,
                                                              PrismPlayer prismPlayer) {
        if (prismPlayer == null) {
            return null;
        }
        if (!name.equals(prismPlayer.getName())) {
            //ok but now names can be used so lets check if an existing player uses that name
            PrismPlayer test = SqlPlayerIdentificationHelper.lookupByName(name);
            if (test != null && test.getUuid() != prismPlayer.getUuid()) {
                Prism.warn("玩家 " + name + " 的 UUID 与另一玩家: " + test.getUuid() + " 的冲突. " +
                        "我们正在尝试在允许此缓存前更新此 UUID 为新的名称.");
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(test.getUuid());
                test.setName(offlinePlayer.getName());
                if (test.getName().equals(name)) {
                    Prism.warn("玩家似乎有着相同的名字 "
                            + "- 通常这在正版服务器上不可能发生.");
                }
                SqlPlayerIdentificationHelper.updatePlayer(test);
            }
            prismPlayer.setName(name);
            SqlPlayerIdentificationHelper.updatePlayer(prismPlayer);
        }
        if (!uuid.equals(prismPlayer.getUuid())) {
            Prism.warn("玩家 " + name + " 的 UUID 与缓存不匹配! " + uuid
                    + " 与缓存 " + prismPlayer.getName() + " / " + prismPlayer.getUuid());

            // Update anyway...
            prismPlayer.setUuid(uuid);
            SqlPlayerIdentificationHelper.updatePlayer(prismPlayer);
        }
        return prismPlayer;
    }

}