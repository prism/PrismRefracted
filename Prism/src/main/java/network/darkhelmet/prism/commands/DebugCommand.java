package network.darkhelmet.prism.commands;

import com.zaxxer.hikari.HikariDataSource;
import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.commandlibs.SubHandler;
import network.darkhelmet.prism.database.PrismDataSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.kitteh.pastegg.PasteBuilder;
import org.kitteh.pastegg.PasteContent;
import org.kitteh.pastegg.PasteFile;
import org.kitteh.pastegg.Visibility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebugCommand implements SubHandler {

    @Override
    public void handle(CallInfo call) {
        if (call.getArgs().length == 2) {
            String arg = call.getArg(1);
            switch (arg.toLowerCase()) {
                case "on":
                case "开":
                    Prism.setDebug(true);
                    break;
                case "off":
                case "关":
                    Prism.setDebug(false);
                    break;
                default: //toggle.
                    Prism.setDebug(!Prism.isDebug());
                    break;
            }
            Prism.messenger.sendMessage(call.getSender(), Prism.messenger.playerMsg(
                    Component.text(Il8nHelper.getRawMessage("debug-msg") + " " + Prism.isDebug())));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Prism.getInstance(), () -> createPaste(
                call.getSender()));
    }

    private String getFile(Path file) {
        try {
            String out = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            out = removePatterns("hostname: (.*)",out,"**secret.host**");
            out = removePatterns("username: (.*)",out,"**username**");
            return removePatterns("password: (.*)",out,"**password**");
        } catch (IOException e) {
            return ExceptionUtils.getStackTrace(e);
        }

    }

    private String removePatterns(String patten, String haystack, String replacement) {
        Pattern p = Pattern.compile(patten);
        Matcher matcher = p.matcher(haystack);
        while (matcher.find()) {
            haystack = haystack.replaceFirst(matcher.group(1),replacement);
        }
        return haystack;
    }

    private String getMainInfo() {
        StringBuilder mainInfo = new StringBuilder();
        mainInfo.append(Bukkit.getName()).append(" 版本: ").append(Bukkit.getServer()
                .getVersion()).append(System.lineSeparator());
        mainInfo.append("插件版本: ").append(Prism.getInstance().getDescription()
                .getVersion()).append(System.lineSeparator());
        mainInfo.append("Java 版本: ").append(System.getProperty("java.version")).append('\n');
        mainInfo.append(System.lineSeparator());
        mainInfo.append("插件列表:").append(System.lineSeparator());
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            mainInfo.append(' ').append(plugin.getName()).append(" - ").append(
                    plugin.getDescription().getVersion()).append(System.lineSeparator());
            mainInfo.append("  ").append(plugin.getDescription().getAuthors()).append(System.lineSeparator());
        }
        return mainInfo.toString();
    }

    private String getDataSourceInfo() {
        PrismDataSource dataSource = Prism.getPrismDataSource();
        StringBuilder out = new StringBuilder();
        String name = dataSource.getClass().getName();
        out.append("数据源名称: ").append(name).append(System.lineSeparator());
        if (dataSource.getDataSource() instanceof HikariDataSource) {
            HikariDataSource ds = (HikariDataSource) dataSource.getDataSource();
            out.append("运行中: ").append(ds.isRunning())
                    .append("总共连接数: ")
                    .append(ds.getHikariPoolMXBean().getTotalConnections())
                    .append(System.lineSeparator())
                    .append("总共连接数: ")
                    .append(ds.getHikariPoolMXBean().getActiveConnections())
                    .append(System.lineSeparator());
        }
        out.append("非法方块:").append(System.lineSeparator());
        for (Material mat : Prism.getIllegalBlocks()) {
            out.append("   ").append(mat.name()).append(System.lineSeparator());
        }
        out.append("追踪世界数: ").append(Prism.prismWorlds.size()).append(System.lineSeparator());
        out.append("追踪玩家数: ").append(Prism.prismPlayers.size()).append(System.lineSeparator());
        out.append("使用工具的玩家: ").append(Prism.playersWithActiveTools.size())
                .append(System.lineSeparator());
        return out.toString();
    }


    private void createPaste(CommandSender sender) {
        Path dataPath = Prism.getInstance().getDataFolder().toPath();
        Path prismConfig = dataPath.resolve("config.yml");
        Path hikariProps = dataPath.resolve("hikari.properties");
        Path prismLog = dataPath.resolve("prism.log");
        String pLog;
        if (prismLog.toFile().length() < 2000000L) {
            pLog = getFile(prismLog);
        } else {
            pLog = "由于文件过大而被裁剪: 您么能需要手动粘贴. <pluginDir>/prism.log";
        }
        PasteBuilder.PasteResult result = new PasteBuilder().name("Prism 调试信息输出")
                .visibility(Visibility.UNLISTED)
                .setApiKey(Prism.getPasteKey())
                .expires(ZonedDateTime.now().plusDays(1)) //1 day
                .addFile(new PasteFile("主要信息",
                        new PasteContent(PasteContent.ContentType.TEXT, getMainInfo())))
                .addFile(new PasteFile("config.yml",
                        new PasteContent(PasteContent.ContentType.TEXT, getFile(prismConfig))))
                .addFile(new PasteFile("hikari.properties",
                        new PasteContent(PasteContent.ContentType.TEXT, getFile(hikariProps))))
                .addFile(new PasteFile("数据源配置",
                        new PasteContent(PasteContent.ContentType.TEXT, getDataSourceInfo())))
                .addFile(new PasteFile("Prism 日志",
                        new PasteContent(PasteContent.ContentType.TEXT, pLog)))
                .build();
        if (result.getPaste().isPresent()) {
            String pasteUrl = "https://paste.gg/" + result.getPaste().get().getId();
            Prism.messenger.sendMessage(sender,
                    Prism.messenger.playerMsg(Il8nHelper.getMessage("paste-output")
                            .replaceFirstText(Pattern.compile("<pasteUrl>"), builder ->
                                    Component.text()
                                            .content(pasteUrl)
                                            .clickEvent(ClickEvent.openUrl(pasteUrl)))));
            Prism.log("粘贴箱已创建 : " + pasteUrl);
            result.getPaste().get().getDeletionKey().ifPresent(
                  s -> {
                          Prism.messenger.sendMessage(sender, Prism.messenger.playerMsg(
                                  Il8nHelper.getMessage("delete-key")
                                          .replaceFirstText(Pattern.compile("<deletekey>"), builder ->
                                                  Component.text()
                                                          .content(s)
                                                          .clickEvent(ClickEvent.copyToClipboard(s)))));
                          Prism.log("删除 key:" + s);
                  }
            );
        } else {
            Prism.messenger.sendMessage(sender,
                    Prism.messenger.playerError(Il8nHelper.getMessage("debug-paste-error")));
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{"调试帮助"};
    }

    @Override
    public String getRef() {
        return ".html";
    }
}
