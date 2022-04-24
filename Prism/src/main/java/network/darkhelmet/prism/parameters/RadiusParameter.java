package network.darkhelmet.prism.parameters;

import network.darkhelmet.prism.ApiHandler;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.api.actions.PrismProcessType;
import network.darkhelmet.prism.bridge.WorldEditBridge;
import network.darkhelmet.prism.utils.ChunkUtils;
import network.darkhelmet.prism.utils.MiscUtils;
import network.darkhelmet.prism.utils.TypeUtils;
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
                            Prism.messenger.playerError("强制半径为 " + radius + " , 即配置中允许的值."));
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
            if (player == null && /* Allow r:global in console */ !inputValue.equals("global")) {
                throw new IllegalArgumentException(
                        "半径参数必须配合一个玩家使用. "
                                + "如果想限制到一个世界内, 使用 w:世界名."
                                + "如果想应用到整个服务器, 使用 半径:全局(r:global).");
            }

            // User wants an area inside of a worldedit selection
            switch (inputValue) {
                case "we":

                    if (ApiHandler.worldEditPlugin == null) {
                        throw new IllegalArgumentException(
                                "因为 Prism 找不到 WorldEdit, 此功能已禁用.");
                    } else {

                        // Load a selection from world edit as our area.
                        if (!WorldEditBridge.getSelectedArea(Prism.getInstance(), player, query)) {
                            throw new IllegalArgumentException(
                                    "无效的选区. 请确保您选中了选区,"
                                            + " 并且选区未超过最大半径.");
                        }
                    }
                    break;

                // Confine to the chunk
                case "c":
                case "chunk":
                case "区块":

                    final Chunk ch = player.getLocation().getChunk();
                    query.setWorld(ch.getWorld().getName());
                    query.setMinLocation(ChunkUtils.getChunkMinVector(ch));
                    query.setMaxLocation(ChunkUtils.getChunkMaxVector(ch));

                    break;

                // User wants no radius, but contained within the current world
                case "world":
                case "世界":
                    // Do they have permission to override the global lookup radius
                    if (query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !player.hasPermission("prism.override-max-lookup-radius")) {
                        throw new IllegalArgumentException("您没有权限覆写最大半径.");
                    }
                    // Do they have permission to override the global applier radius
                    if (!query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !player.hasPermission("prism.override-max-applier-radius")) {
                        throw new IllegalArgumentException("您没有权限覆写最大半径.");
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
                case "全局":
                    // Do they have permission to override the global lookup radius
                    if (query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !sender.hasPermission("prism.override-max-lookup-radius")) {
                        throw new IllegalArgumentException("您没有权限覆写最大半径.");
                    }
                    // Do they have permission to override the global applier radius
                    if (!query.getProcessType().equals(PrismProcessType.LOOKUP)
                            && !sender.hasPermission("prism.override-max-applier-radius")) {
                        throw new IllegalArgumentException("您没有权限覆写最大半径.");
                    }
                    // Either they have permission or player is null
                    query.setWorld(null);
                    query.setAllowNoRadius(true);

                    break;
                default:
                    throw new IllegalArgumentException(
                            "无效的半径. 有很多方式可以定义半径, 请使用 `/prism 行为(actions)` 以获取帮助.");
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