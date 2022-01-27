package network.darkhelmet.prism.formatters;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.displays.DisplayFormatter;

import org.bukkit.OfflinePlayer;

public class ActivityFormatter extends OutputFormatter implements DisplayFormatter<IActivity> {
    /**
     * Cache the minus sign parsed component.
     */
    private final Component signMinus;

    /**
     * Cache the plus sign parsed component.
     */
    private final Component signPlus;

    /**
     * Construct a new activity formatter.
     */
    public ActivityFormatter() {
        super(Prism.getInstance().config().outputs());

        // Compile and cache some templates that won't change per-row
        signMinus = MiniMessage.get().parse(outputConfiguration.signMinus());
        signPlus = MiniMessage.get().parse(outputConfiguration.signPlus());
    }

    @Override
    public Component format(IActivity activity) {
        Component sign = signPlus;
        if (activity.action().type().resultType().equals(ActionResultType.REMOVES)) {
            sign = signMinus;
        }

        Template actionFamilyTemplate = Template.of("actionFamily", actionFamily(activity.action().type().key()));
        Template actionPastTenseTemplate = Template.of("actionPastTense", actionFamily(activity.action().type().key()));
        Template causeTemplate = Template.of("cause", cause(activity.cause()));
        Template signTemplate = Template.of("sign", sign);
        Template materialTemplate = Template.of("content", activity.action().formatContent());
        Template countTemplate = Template.of("count", "1");
        Template sinceTemplate = Template.of("since", since(activity.timestamp()));

        List<Template> templates = List.of(
            actionFamilyTemplate, actionPastTenseTemplate, signTemplate, causeTemplate, materialTemplate,
            countTemplate, sinceTemplate);
        return MiniMessage.get().parse(outputConfiguration.activity(), templates);
    }

    /**
     * Convert the cause into a text string.
     *
     * @param cause The cause
     * @return The cause name/string
     */
    protected String cause(Object cause) {
        String causeName = null;
        if (cause instanceof String) {
            causeName = (String) cause;
        } else if (cause instanceof OfflinePlayer offlinePlayer) {
            causeName = offlinePlayer.getName();
        }

        return causeName;
    }

    /**
     * Get the action family. "break" for "block-break"
     *
     * @param typeKey The action type key
     * @return The action family
     */
    protected String actionFamily(String typeKey) {
        String[] segments = typeKey.split("-");

        return segments[segments.length - 1];
    }

    /**
     * Get the shorthand syntax for time since.
     *
     * @param timestamp The timestamp
     * @return The time since
     */
    protected String since(long timestamp) {
        long diffInSeconds = System.currentTimeMillis() / 1000 - timestamp;

        if (diffInSeconds < 60) {
            return "jsut now";
        }

        long period = 24 * 60 * 60;

        final long[] diff = {
            diffInSeconds / period,
            (diffInSeconds / (period /= 24)) % 24,
            (diffInSeconds / (period / 60)) % 60
        };

        StringBuilder timeAgo = new StringBuilder();

        if (diff[0] > 0) {
            timeAgo.append(diff[0]).append('d');
        }

        if (diff[1] > 0) {
            timeAgo.append(diff[1]).append('h');
        }

        if (diff[2] > 0) {
            timeAgo.append(diff[2]).append('m');
        }

        // 'time_ago' will have something at this point
        return timeAgo.append(" ").append("ago").toString();
    }

    @Override
    public Component heading() {
        return Component.empty();
    }

    @Override
    public Component noResults() {
        return Component.empty();
    }
}
