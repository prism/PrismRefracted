package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.ActionMessage;
import network.darkhelmet.prism.actionlibs.QueryResult;
import network.darkhelmet.prism.api.actions.Handler;
import network.darkhelmet.prism.api.commands.Flag;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.commandlibs.SubHandler;
import network.darkhelmet.prism.utils.MiscUtils;
import network.darkhelmet.prism.utils.TypeUtils;
import net.kyori.adventure.identity.Identity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class PageCommand implements SubHandler {

    private final Prism plugin;

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public PageCommand(Prism plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(CallInfo call) {

        // Is there anything even stored to paginate?
        String keyName = "console";
        if (call.getSender() instanceof Player) {
            keyName = call.getSender().getName();
        }
        if (!plugin.cachedQueries.containsKey(keyName)) {
            Prism.messenger.sendMessage(call.getSender(), Prism.messenger
                    .playerError("您没有被保存的查询可以来翻页. 也许它们过期了? 试试再查询一次."));
            return;
        }

        // Get stored results
        final QueryResult results = plugin.cachedQueries.get(keyName);

        if (call.getArgs().length != 2) {
            Prism.getAudiences().sender(call.getSender())
                    .sendMessage(Identity.nil(),
                          Prism.messenger.playerError("请指定一个页码. 例如 /prism page 2"));
            return;
        }

        // Determine page number
        int page;
        if (TypeUtils.isNumeric(call.getArg(1))) {
            page = Integer.parseInt(call.getArg(1));
        } else {

            if (call.getArg(1).equals("next") || call.getArg(1).equals("n") || call.getArg(1).equals("下")) {
                page = results.getPage() + 1;
            } else if (call.getArg(1).equals("prev") || call.getArg(1).equals("p") || call.getArg(1).equals("上")) {
                if (results.getPage() <= 1) {
                    Prism.messenger.sendMessage(call.getSender(),
                            Prism.messenger.playerError("没有上一页了."));
                    return;
                }
                page = results.getPage() - 1;
            } else {
                Prism.messenger.sendMessage(call.getSender(), Prism.messenger
                        .playerError("页码数必须为一个数值, 或者 下(next)/上(prev)."
                                + " 例如 /prism page 2"));
                return;
            }
        }

        // No negatives
        if (page <= 0) {
            Prism.messenger.sendMessage(call.getSender(),
                    Prism.messenger.playerError("页码数必须大于0."));
            return;
        }

        results.setPage(page);

        // Refresh the query time and replace
        results.setQueryTime();
        plugin.cachedQueries.replace(keyName, results);

        // Results?
        if (results.getActionResults().isEmpty()) {
            Prism.messenger.sendMessage(call.getSender(), Prism.messenger
                    .playerError("没有找到任何数据." + ChatColor.GRAY
                            + " 要么是您错漏了一些东西, 要么是我们."));
            return;
        }

        Prism.messenger.sendMessage(call.getSender(),
                Prism.messenger.playerHeaderMsg(Il8nHelper.formatMessage("lookup-header-message",
                        results.getTotalResults(), results.getPage(), results.getTotalPages())));
        final List<Handler> paginated = results.getPaginatedActionResults();
        if (paginated == null || paginated.size() == 0) {
            Prism.messenger.sendMessage(call.getSender(),
                    Prism.messenger.playerError("无法在此页码中找到任何东西. 请检查您是否输入了正确的页码."));
            return;
        }

        // Show it!
        int resultCount = results.getIndexOfFirstResult();
        for (final Handler a : paginated) {
            final ActionMessage am = new ActionMessage(a);
            if (results.getParameters().hasFlag(Flag.EXTENDED)
                    || plugin.getConfig().getBoolean("prism.messenger.always-show-extended")) {
                am.showExtended();
            }
            am.setResultIndex(resultCount);
            MiscUtils.sendClickableTpRecord(am, call.getSender());
            resultCount++;
        }
        MiscUtils.sendPageButtons(results, call.getSender());

    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-pg-nav")};
    }

    @Override
    public String getRef() {
        return "/lookups.html#pagination";
    }
}