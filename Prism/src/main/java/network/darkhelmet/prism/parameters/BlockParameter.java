package network.darkhelmet.prism.parameters;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.api.objects.MaterialState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class BlockParameter extends SimplePrismParameterHandler {

    public BlockParameter() {
        super("Block", Pattern.compile("[\\w,:\\[\\]=]+"), "b", "方块");
    }

    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        final String[] inputs = input.split(",");

        boolean fusing = false;
        int index = 0;
        for (String block : inputs) {
            boolean start = block.contains("[");
            boolean end = block.contains("]");

            if (fusing) {
                inputs[index] += ("," + block);
            } else {
                inputs[index++] = block;
            }

            if (start && !end) {
                fusing = true;
                --index;
            } else if (!start && end) {
                fusing = false;
                ++index;
            }
        }

        final String[] blocks = Arrays.copyOfRange(inputs, 0, index);

        if (blocks.length > 0) {
            for (final String b : blocks) {

                // if user provided id:subid
                /*
                 * if (b.contains(":") && b.length() >= 3) { final String[] ids = b.split(":");
                 * Material mat = Material.matchMaterial(ids[0]); if (ids.length == 2 && mat !=
                 * null && TypeUtils.isNumeric(ids[1])) { query.addBlockFilter(mat); } else {
                 * throw new IllegalArgumentException("Invalid block name '" + b +
                 * "'. Try /pr ? for help"); } } else
                 */
                {
                    String[] parts = b.split("\\[", 2);
                    String part1 = parts[0];

                    if (part1.indexOf(':') != -1) {
                        // Trailing colon, ie 'stone_slab:' or 'stone_slab:[half=top]'
                        Prism.messenger.sendMessage(sender, Prism.messenger
                                .playerError("跳过了'" + b + "' 中使用的移除方块格式 '方块:data值'"));

                        continue;
                    }

                    Material mat = Material.matchMaterial(part1.toUpperCase().replace("-", "_"));
                    if (mat != null) {
                        if (parts.length == 1) {
                            query.addBlockFilter(mat);
                        } else {
                            String part2 = '[' + parts[1];

                            try {
                                Bukkit.createBlockData(mat, part2);
                                query.addBlockDataFilter(new MaterialState(mat, part2));
                            } catch (IllegalArgumentException e) {
                                Prism.messenger.sendMessage(sender, Prism.messenger.playerError(
                                        "跳过了非法的方块 data值 '" + part2 + "', 材料为 '" + part1 + "'"));
                            }
                        }
                    } else {
                        try {
                            Integer.parseInt(part1);
                            Prism.messenger.sendMessage(sender, Prism.messenger
                                    .playerError("跳过了 数字ID '" + part1 + "', 请使用材料来代替"));
                        } catch (NumberFormatException e) {
                            Prism.messenger.sendMessage(sender,
                                    Prism.messenger.playerError("跳过了未知的材料 '" + part1 + "'"));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected List<String> tabComplete(String alias, String partialParameter, CommandSender sender) {
        List<String> result = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.name().toLowerCase(Locale.ENGLISH).startsWith(partialParameter)) {
                result.add(mat.name().toLowerCase(Locale.ENGLISH));
            }
        }
        return result;
    }

}