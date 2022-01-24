package network.darkhelmet.prism.api.displays;

import net.kyori.adventure.text.Component;

public interface DisplayFormatter<T> {
    /**
     * Format an object for display.
     *
     * @param object The object
     * @return The component
     */
    Component format(T object);

    /**
     * Format a no-results message for this type.
     *
     * @return The component
     */
    Component noResults();
}
