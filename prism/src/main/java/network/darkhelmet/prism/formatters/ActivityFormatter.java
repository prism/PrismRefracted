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
        Template actionFamilyTemplate = Template.of("actionFamily", row.actionFamily());
        Template actionPastTenseTemplate = Template.of("actionPastTense", row.actionFamily());
        Template causeTemplate = Template.of("cause", row.cause());
        Template signTemplate = Template.of("sign", "+");
        Template materialTemplate = Template.of("material", row.material());
        Template countTemplate = Template.of("count", "1");
        Template sinceTemplate = Template.of("since", row.since());

        List<Template> templates = List.of(
            actionFamilyTemplate, actionPastTenseTemplate, signTemplate, causeTemplate, materialTemplate,
            countTemplate, sinceTemplate);
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
