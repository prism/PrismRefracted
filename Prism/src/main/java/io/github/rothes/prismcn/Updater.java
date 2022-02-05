package io.github.rothes.prismcn;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.botsko.prism.Prism;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Updater {

    private final String VERSION_CHANNCEL = "Stable";
    private final int VERSION_NUMBER = 11;
    private final HashMap<String, Integer> msgTimesMap = new HashMap<>();

    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Prism.getInstance(), () -> {
            try {
                checkJson(getJson());
            } catch (IllegalStateException | NullPointerException ignored) {
//                Prism.warn("§c无法正常解析版本信息 Json, 请更新您的插件至最新版本: " + e);
            }
        }, 0L, 72000L);
    }

    private String getJson() {
        try (
                InputStream stream = new URL("https://raw.fastgit.org/Rothes/Prism-Bukkit/master/Version%20Infos.json")
                        .openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ){
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                jsonBuilder.append(line).append("\n");
            }
            return jsonBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkJson(String json) {
        JsonElement element = new JsonParser().parse(json);
        JsonObject root = element.getAsJsonObject();
        JsonObject channels = root.getAsJsonObject("Version_Channels");
        if (channels.has(VERSION_CHANNCEL)) {
            JsonObject channel = channels.getAsJsonObject(VERSION_CHANNCEL);
            if (channel.has("Message")
                    && Integer.parseInt(channel.getAsJsonPrimitive("Latest_Version_Number").getAsString()) > VERSION_NUMBER) {
                sendJsonMessage(channel, "updater");
            }
        } else {
            Prism.warn("§c您使用的插件处于一个未知的版本通道(\"" + VERSION_CHANNCEL + "\"). 请更新您的插件到最新版本.");
        }

        for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("Version_Actions").entrySet()) {
            String[] split = entry.getKey().split("-");
            if (Integer.parseInt(split[1]) > VERSION_NUMBER
                    && VERSION_NUMBER > Integer.parseInt(split[0])) {
                JsonObject message = (JsonObject) entry.getValue();
                if (message.has("Message"))
                    sendJsonMessage(message, entry.getKey());
            }
        }

    }

    private void sendJsonMessage(JsonObject json, String id) {
        JsonObject msgJson = json.getAsJsonObject("Message");
        String msg = msgJson.get("zh-CN").getAsString();

        int msgTimes = json.has("Message_Times") ? json.get("Message_Times").getAsInt() : -1;
        int curTimes = msgTimesMap.get(id) == null? 0 : msgTimesMap.get(id);

        if (msgTimes == -1 || curTimes < msgTimes) {

            String logLevel = json.has("Log_Level") ? json.get("Log_Level").getAsString() : "default maybe";

            for (String s : msg.split("\n")) {
                switch (logLevel) {
                    case "Warn":
                        Prism.warn(s);
                        break;
                    case "Info":
                    default:
                        Prism.log(s);
                        break;
                }
            }
            msgTimesMap.put(id, ++curTimes);
        }

        for (JsonElement action : json.getAsJsonArray("Actions")) {
            if (action.getAsString().equals("Prohibit")) {
                Bukkit.getPluginManager().disablePlugin(Prism.getInstance());
            }
        }

    }

}
