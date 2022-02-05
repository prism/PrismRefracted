package network.darkhelmet.prism.parameters;

import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.utils.DateUtil;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class BeforeParameter extends SimplePrismParameterHandler {


    public BeforeParameter() {
        super("Before", Pattern.compile("[\\w秒分时天周]+"), "before", "早于");
    }

    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        final Long date = DateUtil.translateTimeStringToDate(input);
        if (date != null) {
            query.setBeforeTime(date);
        } else {
            throw new IllegalArgumentException(
                    "无法识别 '早于(before)' 的日期/时长参数值. 请使用 /pr ? 来获取帮助.");
        }
    }
}