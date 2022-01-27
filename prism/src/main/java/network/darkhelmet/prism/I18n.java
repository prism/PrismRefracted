package network.darkhelmet.prism;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

public class I18n {
    /**
     * The translations bundles.
     */
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("languages.messages",
            new UTF8ResourceBundleControl());

    /**
     * Translates a key into a text component.
     *
     * @param key The key
     * @return The component
     */
    public static Component translate(String key) {
        return Component.text(translateStr(key));
    }

    /**
     * Returns the raw string translation for a given key.
     *
     * @param key The key
     * @return The raw message translation
     */
    public static String translateStr(String key) {
        if (resourceBundle == null) {
            return key;
        }

        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            Prism.getInstance().handleException(e);

            return key;
        }
    }

    /**
     * Translates and formats a key.
     *
     * @param key The key
     * @param args Arguments
     * @return The formatted string
     */
    public static String translateStr(String key, Object... args) {
        return String.format(translateStr(key), args);
    }
}