package network.darkhelmet.prism.formatters;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.displays.DisplayFormatter;
import network.darkhelmet.prism.api.storage.models.ActivityRow;

public class ActivityFormatter extends OutputFormatter implements DisplayFormatter<ActivityRow> {
    /**
     * Construct a new activity formatter.
     */
    public ActivityFormatter() {
        super(Prism.getInstance().config().outputs());
    }

    @Override
    public Component format(ActivityRow row) {
        Template actionTemplate = Template.of("action", row.action());

        List<Template> templates = List.of(actionTemplate);
        return MiniMessage.get().parse(outputConfiguration.activity(), templates);
    }

    @Override
    public Component heading() {
        return info("Showing %d results (Page 1 of 1)\n")
            .append(subdued("Using defaults: 3d"));
    }

    @Override
    public Component noResults() {
        return error("no results");
    }
}
