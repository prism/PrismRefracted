package me.botsko.prism.parameters;

import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.utils.TypeUtils;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class IdParameter extends SimplePrismParameterHandler {

    /**
     * Constructor.
     */
    public IdParameter() {
        super("ID", Pattern.compile("[\\d,]+"), "id");
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {

        if (!TypeUtils.isNumeric(input)) {
            throw new IllegalArgumentException("ID 必须为一个数值. 请使用 /pr ? 来获取帮助.");
        }
        query.setId(Long.parseLong(input));
    }
}