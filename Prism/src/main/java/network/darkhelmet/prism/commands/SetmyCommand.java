package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.settings.Settings;
import network.darkhelmet.prism.text.ReplaceableTextComponent;
import network.darkhelmet.prism.utils.ItemUtils;
import network.darkhelmet.prism.wands.Wand;
import io.github.rothes.prismcn.CNLocalization;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SetmyCommand extends AbstractCommand {

    private final Prism plugin;

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public SetmyCommand(Prism plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CallInfo call) {

        String setType = null;
        if (call.getArgs().length >= 2) {
            setType = call.getArg(1);
        }

        if (setType != null && !(setType.equalsIgnoreCase("wand") || setType.equalsIgnoreCase("魔棒"))) {
            Prism.messenger.sendMessage(call.getPlayer(),
                    Prism.messenger.playerError("无效的参数. 使用 '/pr ?' 获取帮助."));
            return;
        }

        if (!plugin.getConfig().getBoolean("prism.wands.allow-user-override")) {
            Prism.messenger.sendMessage(call.getPlayer(),
                    Prism.messenger.playerError("抱歉, 现在暂不允许个性化魔棒."));
        }

        // Check for any wand permissions. @todo There should be some central
        // way to handle this - some way to centralize it at least
        if (checkNoPermissions(call.getPlayer(), "prism.rollback", "prism.restore",
                "prism.wand.*", "prism.wand.inspect", "prism.wand.profile", "prism.wand.rollback",
                "prism.wand.restore")) {
            return;
        }

        // Disable any current wand
        if (Prism.playersWithActiveTools.containsKey(call.getPlayer().getName())) {
            final Wand oldwand = Prism.playersWithActiveTools.get(call.getPlayer().getName());
            oldwand.disable(call.getPlayer());
            Prism.playersWithActiveTools.remove(call.getPlayer().getName());
            WandCommand.sendWandStatus(call.getPlayer(), "wand-current", false, "", "");
        }

        String setSubType = null;
        if (call.getArgs().length >= 3) {
            setSubType = call.getArg(2).toLowerCase();
        }

        if (setSubType != null && (setSubType.equals("mode") || setSubType.equals("模式"))) {

            String setWandMode = null;
            String localisation = null;
            if (call.getArgs().length >= 4) {
                setWandMode = call.getArg(3);
                switch (setWandMode) {
                    case "hand":
                    case "空手":
                        setWandMode = "hand";
                        localisation = "空手";
                        break;
                    case "item":
                    case "物品":
                        setWandMode = "item";
                        localisation = "物品";
                        break;
                    case "block":
                    case "方块":
                        setWandMode = "block";
                        localisation = "方块";
                        break;
                    default:
                }
            }
            if (setWandMode != null
                    && (setWandMode.equals("hand") || setWandMode.equals("item") || setWandMode.equals("block"))) {
                Settings.saveSetting("wand.mode", setWandMode, call.getPlayer());
                Settings.deleteSetting("wand.item", call.getPlayer());
                Prism.messenger.sendMessage(call.getPlayer(), Prism.messenger.playerHeaderMsg(
                        ReplaceableTextComponent.builder("setWandMode")
                                .replace("<wandMode>", localisation,
                                        Style.style(NamedTextColor.GREEN))
                                .build()));
                return;
            }
            Prism.messenger.sendMessage(call.getPlayer(),
                    Prism.messenger.playerError(Il8nHelper.getMessage("invalid-arguments")));
            return;
        }

        if (setSubType != null && (setSubType.equals("item") || setSubType.equals("物品"))) {
            if (call.getArgs().length >= 4) {
                String wandString = call.getArg(3);
                Material setWand = Material.matchMaterial(wandString);

                // If non-material, check for name
                if (setWand == null) {
                    final ArrayList<Material> itemMaterials = Prism.getItems().getMaterialsByAlias(wandString);
                    if (itemMaterials.size() > 0) {
                        setWand = itemMaterials.get(0);
                    } else {
                        Prism.messenger.sendMessage(call.getPlayer(),
                              Prism.messenger.playerError(Il8nHelper.getMessage("item-no-match")));
                        return;
                    }
                }

                String localization = CNLocalization.getMaterialLocale(setWand);
                if (ItemUtils.isBadWand(setWand)) {
                    Prism.messenger.sendMessage(call.getPlayer(),
                            Prism.messenger.playerError(ReplaceableTextComponent.builder("wand-bad")
                                    .replace("<itemName>", localization).build()));
                    return;
                }

                Settings.saveSetting("wand.item", wandString, call.getPlayer());
                Prism.messenger.sendMessage(call.getPlayer(), Prism.messenger.playerHeaderMsg(
                        ReplaceableTextComponent.builder("wand-item-change").replace("<itemName>", localization)
                                .build()));
                return;
            }
        }
        Prism.messenger.sendMessage(call.getPlayer(),
                Prism.messenger.playerError(Il8nHelper.getMessage("invalid-arguments")));
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-wand-set")};
    }

    @Override
    public String getRef() {
        return "/wand.html#setting-resetting-the-wand";
    }
}