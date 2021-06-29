package me.botsko.prism.database;

import org.bukkit.command.CommandSender;

public interface BlockReportQuery extends SelectQuery {
    void report(CommandSender sender);
}
