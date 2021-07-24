package me.botsko.prism.parameters;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.utils.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class SinceParameter extends SimplePrismParameterHandler {

    /**
     * Time since parameter.
     */
    public SinceParameter() {
        super("Since", Pattern.compile("[\\w秒分时天周]+"), "t", "since", "自从", "时长");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        if (input.equalsIgnoreCase("none") && input.equalsIgnoreCase("无")) {
            query.setIgnoreTime(true);
        } else {
            final Long date = DateUtil.translateTimeStringToDate(input);
            if (date != null) {
                query.setSinceTime(date);
            } else {
                throw new IllegalArgumentException(
                        "无法识别 '自从(since)' 的日期/时长参数值. 请使用 /pr ? 来获取帮助.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void defaultTo(QueryParameters query, CommandSender sender) {

        if (query.getProcessType().equals(PrismProcessType.DELETE)) {
            return;
        }

        if (!query.getFoundArgs().contains("before") && !query.getFoundArgs().contains("since")) {

            final FileConfiguration config = Bukkit.getPluginManager().getPlugin("Prism").getConfig();

            Long date = DateUtil.translateTimeStringToDate(config.getString("prism.queries.default-time-since"));
            if (date <= 0L) {
                Prism.log("错误 - 时长范围配置 prism.time-since 非法");
                date = DateUtil.translateTimeStringToDate("3d");
            }
            query.setSinceTime(date);
            query.addDefaultUsed("t:" + config.getString("prism.queries.default-time-since"));
        }
    }
}