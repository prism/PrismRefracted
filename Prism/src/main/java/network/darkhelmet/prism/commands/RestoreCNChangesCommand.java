package network.darkhelmet.prism.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.commandlibs.SubHandler;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.WeakHashMap;

public class RestoreCNChangesCommand implements SubHandler {

    private final WeakHashMap<CommandSender, Long> time = new WeakHashMap<>();

    private final Prism plugin;

    public RestoreCNChangesCommand(Prism plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CallInfo call) {
        CommandSender sender = call.getSender();
        if (!sender.isOp()) {
            sendMsg(sender, "此指令仅 OP 可执行.");
            return;
        }
        String[] args = call.getArgs();
        if (args.length == 1) {
            sendMsg(sender, "您正在还原 Prism 中文版对数据库架构的所有更改.");
            sendMsg(sender, "还原更改后, 您可以回到 Prism 官方英文版,");
            sendMsg(sender, "并且可以保证之后英文版插件使用一切正常.");
            sendMsg(sender, "目前中文版的数据库更改包括:");
            sendMsg(sender, "- 记录行为是否已回滚, 并在记录查询中突出显示;");
            sendMsg(sender, "- 对非玩家名称进行翻译, 如 '爬行者', '环境' 等.");
            sendMsg(sender, "完成还原后, 我们将立即关闭插件, 之后玩家的所有");
            sendMsg(sender, "行为以及队列中的行为都不会记录到数据库.");
            sendMsg(sender, Component.text("注意: 此操作一经执行, 不可恢复. 所有中文版特有的").color(NamedTextColor.RED));
            sendMsg(sender, Component.text("数据将永久清除. 完成还原后请尽快重启服务器.").color(NamedTextColor.RED));
            sendMsg(sender, "若要确认执行, 请在 10 秒内输入 /pr restorecnchanges confirm");
            time.put(sender, System.currentTimeMillis());
        } else if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            Long time = this.time.get(sender);
            if (time == null || (System.currentTimeMillis() - time) > 1000L * 10) {
                sendMsg(sender, "不存在确认操作或已超时. 请先执行 /pr restorecnchanges");
            } else {
                sendMsg(sender, "正在还原数据库架构更改...");
                plugin.restoreCNChanges(sender);
            }
        } else {
            sendMsg(sender, "请执行 /pr restorecnchanges");
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    private void sendMsg(CommandSender sender, Component component) {
        Prism.messenger.sendMessage(sender, Prism.messenger.playerHeaderMsg(component));
    }

    private void sendMsg(CommandSender sender, String text) {
        sendMsg(sender, Component.text(text));
    }

    /**
     * Returns a short help message.
     *
     * @return String
     */
    @Override
    public String[] getHelp() {
        return new String[]{"还原中文版数据库架构更改"};
    }

    /**
     * This should return the web reference to documentation.
     *
     * @return String
     */
    @Override
    public String getRef() {
        return ".html";
    }

}
