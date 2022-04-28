package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.commandlibs.Executor;
import network.darkhelmet.prism.commandlibs.SubHandler;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PrismCommands extends Executor {

    /**
     * Constructor.
     *
     * @param prism Plugin.
     */
    public PrismCommands(Plugin prism, boolean failed) {
        super(prism, "subcommand", "prism");
        setupCommands(failed);
    }

    private void setupCommands(boolean failed) {
        final Prism prism = (Prism) plugin;
        addSub(new String[]{"about", "default", "关于"}, "prism.help").allowConsole().setHandler(new AboutCommand(prism));
        addSub(new String[]{"debug", "调试"}, "prism.debug").allowConsole().setHandler(new DebugCommand());
        addSub(new String[]{"help", "?", "帮助"}, "prism.help").allowConsole().setHandler(new HelpCommand(failed));
        addSub(new String[]{"flags", "标志"}, "prism.help").allowConsole().setHandler(new FlagsCommand());
        addSub(new String[]{"params", "参数"}, "prism.help").allowConsole().setHandler(new ParamsCommand());
        addSub(new String[]{"actions", "行为"}, "prism.help").allowConsole().setHandler(new ActionsCommand());
        addSub(new String[]{"settings", "设置"}, "prism.settings").allowConsole().setHandler(new SettingCommands());
        addSub(new String[]{"reload", "重载"}, "prism.reload").allowConsole().setHandler(new SubHandler() {

            @Override
            public void handle(CallInfo call) {
                prism.reloadConfig();
                prism.loadConfig();
                Prism.messenger.sendMessage(call.getSender(),
                        Prism.messenger.playerHeaderMsg(Il8nHelper.getMessage("prism-reload-success")));
                if (failed) {
                    Prism.messenger.sendMessage(call.getSender(),
                            Prism.messenger.playerHeaderMsg(Il8nHelper.getMessage("prism-reload-failed")));
                }
            }

            @Override
            public List<String> handleComplete(CallInfo call) {
                return null;
            }

            @Override
            public String[] getHelp() {
                return new String[]{Il8nHelper.getRawMessage("help-reload")};
            }

            @Override
            public String getRef() {
                return "";
            }
        });
        if (failed) {
            return;
        }
        addSub(new String[]{"lookup", "l", "查询"}, "prism.lookup").allowConsole().setMinArgs(1)
                .setHandler(new LookupCommand(prism));
        addSub(new String[]{"near", "附近"}, "prism.lookup").setHandler(new NearCommand(prism));
        addSub(new String[]{"page", "pg", "页码"}, new String[]{"prism.lookup.paginate", "prism.lookup"}).allowConsole()
                .setMinArgs(1).setHandler(new PageCommand(prism));
        addSub(new String[]{"wand", "w", "i", "inspect", "魔棒", "检查"},
                new String[]{"prism.rollback", "prism.restore", "prism.lookup", "prism.wand.inspect",
                      "prism.wand.profile", "prism.wand.rollback", "prism.wand.restore"})
                .setHandler(new WandCommand(prism));
        addSub(new String[]{"setmy", "偏好"}, new String[]{"prism.setmy.wand"}).setHandler(new SetmyCommand(prism));
        addSub(new String[]{"resetmy", "重置偏好"}, new String[]{"prism.setmy.wand"}).setHandler(new ResetmyCommand(prism));
        addSub(new String[]{"tp", "传送"}, "prism.tp").setMinArgs(1).setHandler(new TeleportCommand(prism));
            addSub(new String[]{"ex", "灭火"}, "prism.extinguish").setHandler(new ExtinguishCommand(prism));
        addSub(new String[]{"drain" ,"排水"}, "prism.drain").setHandler(new DrainCommand(prism));
        addSub(new String[]{"preview", "pv", "预览"}, "prism.preview").setMinArgs(1).setHandler(new PreviewCommand(prism));
        addSub(new String[]{"report", "rp", "报告"}, "prism.report").allowConsole().setHandler(new ReportCommand(prism));
        addSub(new String[]{"rollback", "rb", "回滚"}, "prism.rollback").allowConsole().setMinArgs(1)
                .setHandler(new RollbackCommand(prism));
        addSub(new String[]{"restore", "rs", "还原"}, "prism.restore").allowConsole().setMinArgs(1)
                .setHandler(new RestoreCommand(prism));
        addSub(new String[]{"delete", "purge", "删除", "清理"}, "prism.delete").allowConsole().setHandler(new DeleteCommand(prism));
        addSub(new String[]{"recorder", "记录器"}, "prism.recorder").allowConsole().setHandler(new RecorderCommand(prism));
        addSub(new String[]{"undo", "撤销"}, "prism.rollback").setHandler(new UndoCommand(prism));
        addSub(new String[]{"view", "v", "视图"}, "prism.view").setMinArgs(1).setHandler(new ViewCommand(prism));
        addSub(new String[]{"purge", "清理"}, "prism.purge").allowConsole().setHandler(new PurgeCommand(prism));
        addSub(new String[]{"restorecnchanges"}, "prism.restorecnchanges").allowConsole().setHandler(new RestoreCNChangesCommand(prism));
    }

}
