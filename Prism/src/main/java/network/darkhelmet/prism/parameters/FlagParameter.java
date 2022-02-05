package network.darkhelmet.prism.parameters;

import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.api.commands.Flag;
import network.darkhelmet.prism.utils.TypeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FlagParameter implements PrismParameterHandler {


    @Override
    public String getName() {
        return "Flag";
    }


    @Override
    public String[] getHelp() {
        return new String[0];
    }


    @Override
    public boolean applicable(String parameter, CommandSender sender) {
        return Pattern.compile("(-)([^\\s]+)?").matcher(parameter).matches();
    }

    @Override
    public void process(QueryParameters query, String parameter, CommandSender sender) {
        final String[] flagComponents = parameter.substring(1).split("=");
        Flag flag;
        try {
            flag = Flag.valueOf(flagComponents[0].replace("-", "_").toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("找不到 标记 -" + flagComponents[0] , ex);
        }
        if (!(query.hasFlag(flag))) {

            query.addFlag(flag);

            // Flag has a value
            if (flagComponents.length > 1) {
                if (flag.equals(Flag.PER_PAGE)) {
                    if (TypeUtils.isNumeric(flagComponents[1])) {
                        query.setPerPage(Integer.parseInt(flagComponents[1]));
                    } else {
                        throw new IllegalArgumentException(
                                "每页(Per-page) 标记值必须为一个数值. 请使用 /pr ? 来获取帮助.");
                    }
                } else if (flag.equals(Flag.SHARE)) {
                    for (final String sharePlayer : flagComponents[1].split(",")) {
                        if (sharePlayer.equals(sender.getName())) {
                            throw new IllegalArgumentException("您不能分享查询结果给你自己!");
                        }
                        final Player shareWith = Bukkit.getServer().getPlayer(sharePlayer);
                        if (shareWith != null) {
                            query.addSharedPlayer(shareWith);
                        } else {
                            throw new IllegalArgumentException(
                                    "不能分享给 " + sharePlayer + ". 他们在线吗?");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void defaultTo(QueryParameters query, CommandSender sender) {

    }

    @Override
    public List<String> tabComplete(String partialParameter, CommandSender sender) {
        final String[] flagComponents = partialParameter.substring(1).split("=", 2);
        Flag flag;
        final String name = flagComponents[0].replace("-", "_").toUpperCase();
        try {
            flag = Flag.valueOf(name);
        } catch (final IllegalArgumentException ex) {
            final List<String> completions = new ArrayList<>();
            for (final Flag possibleFlag : Flag.values()) {
                final String flagName = possibleFlag.toString();
                if (flagName.startsWith(name)) {
                    completions.add("-" + flagName.replace('_', '-').toLowerCase());
                }
            }
            return completions;
        }

        // Flag has a value
        if (flagComponents.length <= 1) {
            return null;
        }

        String prefix = "-" + flag.toString().replace('_', '-').toLowerCase() + "=";
        if (flag.equals(Flag.SHARE)) {
            final String value = flagComponents[1];
            final int end = value.lastIndexOf(',');
            String partialName = value;
            if (end != -1) {
                partialName = value.substring(end + 1);
                prefix = prefix + value.substring(0, end) + ",";
            }
            partialName = partialName.toLowerCase();
            final List<String> completions = new ArrayList<>();
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialName)) {
                    completions.add(prefix + player.getName());
                }
            }
            return completions;
        }
        return null;
    }

    @Override
    public boolean hasPermission(String parameter, Permissible permissible) {
        final String[] flagComponents = parameter.substring(1).split("=");
        Flag flag;
        try {
            flag = Flag.valueOf(flagComponents[0].replace("-", "_").toUpperCase());
        } catch (final IllegalArgumentException ex) {
            return false;
        }
        return flag.hasPermission(permissible);
    }
}