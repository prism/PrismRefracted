package network.darkhelmet.prism.formatters;

import net.kyori.adventure.text.Component;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.displays.DisplayFormatter;
import network.darkhelmet.prism.api.storage.models.ActivityRow;

public class ActivityFormatter implements DisplayFormatter<ActivityRow> {
    @Override
    public Component format(ActivityRow row) {
        return Prism.getInstance().outputFormatter().info(row.action());
    }

    @Override
    public Component noResults() {
        return Prism.getInstance().outputFormatter().error("no results");
    }
}
