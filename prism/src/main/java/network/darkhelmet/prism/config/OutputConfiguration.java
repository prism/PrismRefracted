package network.darkhelmet.prism.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class OutputConfiguration {
    @Comment("Used to prefix any other messages with <prefix> placeholders.")
    private String prefix = "<dark_gray>(<#ff55ff>\uFF01<dark_gray>) <#ff55ff>Prism<gray> \u300b";

    @Comment("Used for all \"success\" messages.")
    private String success = "<prefix><green><message>";

    @Comment("Used for all \"error\" messages.")
    private String error = "<prefix><red><message>";

    @Comment("Used for all \"info\" messages.")
    private String info = "<prefix><white><message>";

    @Comment("Used for the heading message before outputting a list (of events, etc).")
    private String heading = "<prefix><white><heading> <underlined><#0ccfcb><bold><message>";

    @Comment("Configure the primary line of activity messages.")
    private String activity = "<action>";

    @Comment("Used for all \"subdued\" messages.")
    private String subdued = "<prefix><gray><message>";

    public String prefix() {
        return prefix;
    }

    public String info() {
        return info;
    }

    public String error() {
        return error;
    }

    public String success() {
        return success;
    }

    public String heading() {
        return heading;
    }

    public String activity() {
        return activity;
    }

    public String subdued() {
        return subdued;
    }
}
