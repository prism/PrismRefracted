package me.botsko.prism.parameters;

import me.botsko.prism.ApiHandler;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.bridge.WorldEditBridge;
import me.botsko.prism.utils.ChunkUtils;
import me.botsko.prism.utils.MiscUtils;
import me.botsko.prism.utils.TypeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class RadiusParameter extends SimplePrismParameterHandler {

    public RadiusParameter() {
        super("Radius", Pattern.compile("[\\w,:-]+"), "r", "半径");
    }

    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        String inputValue = input;

        final FileConfiguration config = Prism.config;

        if (TypeUtils.isNumeric(inputValue) || (inputValue.contains(":") && inputValue.split(":").length >= 1
                && TypeUtils.isNumeric(inputValue.split(":")[1]))) {
            Location coordsLoc = null;
            int desiredRadius;
            if (inputValue.contains(":")) {
                desiredRadius = Integer.parseInt(inputValue.split(":")[1]);
                final String radiusLocOrPlayer = inputValue.split(":")[0];
                if (radiusLocOrPlayer.contains(",") && player != null) { // Coordinates;
                    // x,y,z
                    final String[] coordinates = radiusLocOrPlayer.split(",");
                    if (coordinates.length != 3) {
                        throw new IllegalArgumentException("无法解析坐标 '" + radiusLocOrPlayer
                                + "'. 也许你输入了超过2个逗号?");
                    }
                    for (final String s : coordinates) {
                        if (!TypeUtils.isNumeric(s)) {
                            throw new IllegalArgumentException("坐标 '" + s + "' 不是一个数值.");
                        }
                    }
                    coordsLoc = (new Location(player.getWorld(), Integer.parseInt(coordinates[0]),
                            Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2])));
                } else {
                    // Try to find an online player
                    Player p2 = Bukkit.getServer().getPlayer(radiusLocOrPlayer);
                    if (p2 == null) {
                        throw new IllegalArgumentException("找不到叫做 '" + radiusLocOrPlayer
                                + "' 的玩家. 也许他们不在线, 或者您输错了名字?");
                    }
                    player = p2;
                }
            } else {
                desiredRadius = Integer.parseInt(inputValue);
            }
            if (desiredRadius <= 0) {
                throw new IllegalArgumentException(
                        "半径必须大于 0. 或者忽略它来使用默认值. 请使用 /pr ? 来获取帮助.");
            }

            // If neither sender or a named player found, die here
            if (player == null) {
                throw new IllegalArgumentException(
                        "只有玩家可以使用半径参数. 如果您想限制到一个世界, "
                                + "可以使用 '世界:世界名' 或 'w:世界名'.");
            }

            // Clamp radius based on perms, configs
            int radius = MiscUtils.clampRadius(player, desiredRadius, query.getProcessType(), config);
            if (desiredRadius != radius) {
                if (sender != null) {
                    Prism.messenger.sendMessage(sender,
                            Prism.messenger.playerError("Forcing radius to " + radius + " as allowed by config."));
                }
            }

            if (radius > 0) {
                query.setRadius(radius);
                if (coordsLoc != null) {
                    query.setMinMaxVectorsFromPlayerLocation(coordsLoc); // We
                    // need
                    // to
                    // set
                    // this
                    // *after*
                    // the
                    // radius
                    // has
                    // been
                    // set
                    // or
                    // it
                    // won't
                    // work.
                } else {
                    query.setMinMaxVectorsFromPlayerLocation(player.getLocation());
                }
            }
        } else {

            // If neither sender or a named player found, die here
            if (player == null) {
                throw new IllegalArgumentException(
                        "The radius parameter must be used by a player. "
                                + "Use w:worldname if attempting to limit to a world.");
            }

            // User wants an area inside of a worldedit selection
            switch (inputValue) {
                case "we":

                    if (ApiHandler.worldEditPlugin == null) {
                        throw new IllegalArgumentException(
                                "This feature is disabled because Prism couldn't find WorldEdit.");
                    } else {

                        // Load a selection from world edit as our area.
                        if (!WorldEditBridge.getSelectedArea(Prism.getInstance(), player, query)) {
                            throw new IllegalArgumentException(
                                    "Invalid region selected. Make sure you have a region selected,"
                                            + " and that it doesn't exceed the max radius.");
                        }
                    }
                    break;

                // Confine to the chunk
                case "c":
                case "chunk":

                    final Chunk ch = player.getLocation().getChunk();
                    query.setWorld(ch.getWorld().getName());
                    query.setMinLocation(ChunkUtils.getChunkMinVector(ch));
                    query.setMaxLocation(ChunkUtils.getChunkMaxVector(ch));

                    break;

                // User wants no radius, but contained within the current world
                case "world":
                    // Do they have permission to override the global lookup radius
                    if (query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !player.hasPermission("prism.override-max-lookup-radius")) {
                        throw new IllegalArgumentException("You do not have permission to override the max radius.");
                    }
                    // Do they have permission to override the global applier radius
                    if (!query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !player.hasPermission("prism.override-max-applier-radius")) {
                        throw new IllegalArgumentException("You do not have permission to override the max radius.");
                    }
                    // Use the world defined in the w: param
                    if (query.getWorld() != null) {
                        inputValue = query.getWorld();
                    } else {
                        inputValue = player.getWorld().getName();
                    }
                    query.setWorld(inputValue);
                    query.setAllowNoRadius(true);
                    break;

                // User has asked for a global radius
                case "global":
                    // Do they have permission to override the global lookup radius
                    if (query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !player.hasPermission("prism.override-max-lookup-radius")) {
                        throw new IllegalArgumentException("You do not have permission to override the max radius.");
                    }
                    // Do they have permission to override the global applier radius
                    if (!query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !player.hasPermission("prism.override-max-applier-radius")) {
                        throw new IllegalArgumentException("You do not have permission to override the max radius.");
                    }
                    // Either they have permission or player is null
                    query.setWorld(null);
                    query.setAllowNoRadius(true);

                    break;
                default:
                    throw new IllegalArgumentException(
                            "Radius is invalid. There's a bunch of choice, so use /prism actions for assistance.");
            }
        }
    }

    @Override
    public void defaultTo(QueryParameters query, CommandSender sender) {
        if (query.getProcessType().equals(PrismProcessType.DELETE)) {
            return;
        }
        if (sender instanceof Player) {
            if (!query.allowsNoRadius()) {
                query.setRadius(Prism.config.getInt("prism.queries.default-radius"));
                query.addDefaultUsed("r:" + query.getRadius());
            }
        }
    }
}