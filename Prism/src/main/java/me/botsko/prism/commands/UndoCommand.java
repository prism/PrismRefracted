package me.botsko.prism.commands;

import me.botsko.prism.Il8nHelper;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionMessage;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.actions.PrismProcessAction;
import me.botsko.prism.api.actions.Handler;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.api.commands.Flag;
import me.botsko.prism.appliers.Previewable;
import me.botsko.prism.appliers.PrismApplierCallback;
import me.botsko.prism.appliers.Undo;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.SubHandler;
import me.botsko.prism.utils.TypeUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.ChatColor;

import java.util.List;

public class UndoCommand implements SubHandler {

    private final Prism plugin;

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public UndoCommand(Prism plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CallInfo call) {
        final Audience audience = Prism.getAudiences().player(call.getPlayer());
        if (call.getArgs().length > 1) {

            final ActionsQuery aq = new ActionsQuery(plugin);

            long recordId = 0;
            if (TypeUtils.isNumeric(call.getArg(1))) {
                recordId = Long.parseLong(call.getArg(1));
                if (recordId <= 0) {
                    Prism.messenger.sendMessage(call.getPlayer(),
                            Prism.messenger.playerError("记录ID必须大于0."));
                    return;
                }
            } else {
                if (call.getArg(1).equals("last") || call.getArg(1).equals("上次")) {
                    recordId = aq.getUsersLastPrismProcessId(call.getPlayer().getName());
                }
            }

            // Invalid id
            if (recordId == 0) {
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerError("您可能没有上次操作记录, 或者使用了无效的ID."));
                return;
            }

            final PrismProcessAction process = aq.getPrismProcessRecord(recordId);
            if (process == null) {
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerError("不存在此值的操作记录."));
                return;
            }

            // We only support this for drains
            if (!process.getProcessChildActionType().equals("prism-drain")) {
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerError("目前您仅可以撤销排水操作."));
                return;
            }

            // Pull the actual block change data for this undo event
            final QueryParameters parameters = new QueryParameters();
            parameters.setWorld(call.getPlayer().getWorld().getName());
            parameters.addActionType(process.getProcessChildActionType());
            parameters.addPlayerName(call.getPlayer().getName());
            parameters.setParentId(recordId);
            parameters.setProcessType(PrismProcessType.UNDO);

            // make sure the distance isn't too far away

            final QueryResult results = aq.lookup(parameters, call.getPlayer());
            if (!results.getActionResults().isEmpty()) {

                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerHeaderMsg(Il8nHelper.getMessage("command-undo-complete")));

                final Previewable rb = new Undo(plugin, call.getPlayer(), results.getActionResults(), parameters,
                        new PrismApplierCallback());
                rb.apply();

            } else {
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerError("没有找到任何可以撤销的东西. 这一定是 Prism 的问题."));
            }

        } else {

            // Show the list
            // Process and validate all of the arguments
            final QueryParameters parameters = new QueryParameters();
            parameters.setAllowNoRadius(true);
            parameters.addActionType("prism-process");
            parameters.addPlayerName(call.getPlayer().getName());
            parameters.setLimit(5); // @todo config this, and move the logic
            // to queryparams

            final ActionsQuery aq = new ActionsQuery(plugin);
            final QueryResult results = aq.lookup(parameters, call.getPlayer());
            if (!results.getActionResults().isEmpty()) {
                Prism.messenger.sendMessage(call.getPlayer(),Prism.messenger.playerHeaderMsg(
                        Il8nHelper.formatMessage("lookup-header-message",
                                results.getTotalResults(), 1, results.getTotalPages())));
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerSubduedHeaderMsg(Il8nHelper.getMessage("command-undo-help")));

                final List<Handler> paginated = results.getPaginatedActionResults();
                if (paginated != null) {
                    for (final Handler a : paginated) {
                        final ActionMessage am = new ActionMessage(a);
                        if (parameters.hasFlag(Flag.EXTENDED)
                                || plugin.getConfig().getBoolean("prism.messenger.always-show-extended")) {
                            am.showExtended();
                        }
                        Prism.messenger.sendMessage(call.getPlayer(),Prism.messenger.playerMsg(am.getMessage()));
                    }
                } else {
                    Prism.messenger.sendMessage(call.getPlayer(),Prism.messenger
                            .playerError("无法在此页码中找到任何东西. 请检查您是否输入了正确的页码."));
                }
            } else {
                Prism.messenger.sendMessage(call.getPlayer(),Prism.messenger.playerError(
                        "没有找到任何数据." + ChatColor.GRAY + " 要么是您错漏了一些东西, 要么是我们."));
            }
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-undo")};
    }

    @Override
    public String getRef() {
        return "/undo.html";
    }
}