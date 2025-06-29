package network.darkhelmet.prism.utils;

import com.google.common.base.CaseFormat;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.ActionMessage;
import network.darkhelmet.prism.actionlibs.QueryResult;
import network.darkhelmet.prism.api.actions.PrismProcessType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MiscUtils {

    /**
     * Placing this here so it's easier to share the logic.
     *
     * @param player        Player
     * @param desiredRadius integer
     * @param processType   {@link PrismProcessType}
     * @param config        {@link FileConfiguration}
     * @return integer
     */
    public static int clampRadius(Player player, int desiredRadius, PrismProcessType processType,
                                  ConfigurationSection config) {

        if (desiredRadius <= 0) {
            return config.getInt("prism.near.default-radius");
        }

        // Safety checks for max lookup radius
        int maxLookupRadius = config.getInt("prism.queries.max-lookup-radius");
        if (maxLookupRadius <= 0) {
            maxLookupRadius = 5;
            Prism.log("Max lookup radius may not be lower than one. Using safe inputue of five.");
        }

        // Safety checks for max applier radius
        int maxApplierRadius = config.getInt("prism.queries.max-applier-radius");
        if (maxApplierRadius <= 0) {
            maxApplierRadius = 5;
            Prism.log("Max applier radius may not be lower than one. Using safe inputue of five.");
        }

        // Does the radius exceed the configured max?
        if (processType.equals(PrismProcessType.LOOKUP) && desiredRadius > maxLookupRadius) {
            // If player does not have permission to override the max
            if (player != null && !player.hasPermission("prism.override-max-lookup-radius")) {
                return maxLookupRadius;
            }
            // Otherwise non-player
            return desiredRadius;
        } else if (!processType.equals(PrismProcessType.LOOKUP) && desiredRadius
                > maxApplierRadius) {
            // If player does not have permission to override the max
            if (player != null && !player.hasPermission("prism.override-max-applier-radius")) {
                return maxApplierRadius;
            }
            // Otherwise non-player
            return desiredRadius;
        } else {
            // Otherwise, the radius is valid and is not exceeding max
            return desiredRadius;
        }
    }

    /**
     * Get an enum that extends T as a fall back class.
     *
     * @param from     String
     * @param fallback the Class to fallback too
     * @param <T>      THe Class extending Enum
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnum(String from, T fallback) {
        if (from != null) {
            try {
                return (T) Enum.valueOf(fallback.getClass(), from.toUpperCase());
            } catch (IllegalArgumentException e) {
                Prism.debug(e.getMessage());
            }
        }
        return fallback;
    }

    public static String niceName(String in) {
        String[] parts = in.replace('_', ' ').trim().split("", 2);
        return parts[0].toUpperCase() + parts[1].toLowerCase();
    }

    /**
     * Send clickable record.
     *
     * @param a      ActionMessage
     * @param player CommandSender
     */
    public static void sendClickableTpRecord(ActionMessage a, CommandSender player) {
        Prism.messenger.sendMessage(player, a.getMessage());
    }

    /**
     * Send page buttons.
     *
     * @param results Results
     * @param player  Player
     */
    public static void sendPageButtons(QueryResult results, CommandSender player) {
        if (player instanceof Player) {
            if (results.getPage() == 1) {
                if (results.getTotalPages() > 1) {
                    Prism.messenger.sendMessage(player, getNextButton());
                }
            } else if (results.getPage() < results.getTotalPages()) {
                Prism.messenger.sendMessage(player, MiscUtils.getPrevNextButtons());
            } else if (results.getPage() == results.getTotalPages()) {
                Prism.messenger.sendMessage(player, MiscUtils.getPreviousButton());
            }
        }
    }

    /**
     * Gets a list of strings starting with.
     *
     * @param start         String
     * @param options       Options
     * @param caseSensitive if case sensitive
     * @return List of Strings
     */
    public static List<String> getStartingWith(final String start, Iterable<String> options,
                                               boolean caseSensitive) {
        final List<String> result = new ArrayList<>();
        if (caseSensitive) {
            for (final String option : options) {
                if (option.startsWith(start)) {
                    result.add(option);
                }
            }
        } else {
            String caseStart = start.toLowerCase();
            for (final String option : options) {
                if (option.toLowerCase().startsWith(caseStart)) {
                    result.add(option);
                }
            }
        }

        return result;
    }

    public static List<String> getStartingWith(String arg, Iterable<String> options) {
        return getStartingWith(arg, options, true);
    }

    /**
     * Send alert.
     *
     * @param msg      the message
     * @param commands the commands
     */
    public static void dispatchAlert(String msg, Iterable<String> commands) {
        String cleanMessage = PlainComponentSerializer.plain().deserialize(msg).content();
        for (String command : commands) {
            if ("examplecommand <alert>".equals(command)) {
                continue;
            }
            String processedCommand = command.replace("<alert>", cleanMessage);
            Bukkit.getScheduler().runTask(Prism.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand));
        }
    }

    /**
     * FInd a nice name for entity.
     *
     * @param entity Entity
     * @return String
     */
    public static String getEntityName(Entity entity) {
        if (entity == null) {
            return "unknown";
        }
        if (entity.getType() == EntityType.PLAYER) {
            return entity.getName();
        }
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, entity.getType().name());
    }

    /**
     * Prev Button.
     *
     * @return TextComponent
     */
    public static TextComponent getPreviousButton() {
        return Component.text(" [<< Prev]")
                .color(TextColor.fromHexString("#ef9696"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to view the previous page")))
                .clickEvent(ClickEvent.runCommand("/pr pg p"));
    }

    /**
     * NExt button.
     *
     * @return TextComponent
     */
    public static TextComponent getNextButton() {
        return Component.text("           ")
                .color(TextColor.fromHexString("#01a960"))
                .append(MiscUtils::getNextButtonComponent);
    }

    /**
     * Next Button.
     *
     * @return BaseComponent.
     */
    private static TextComponent getNextButtonComponent() {
        return Component.text("[Next >>]")
                .hoverEvent(HoverEvent.showText(Component.text("Click to view the next page")))
                .color(TextColor.fromHexString("#01a960"))
                .clickEvent(ClickEvent.runCommand("/pr pg n"));
    }

    /**
     * Prev Button.
     *
     * @return BaseComponent.
     */
    public static TextComponent getPrevNextButtons() {
        TextComponent divider = Component.text(" | ")
                .color(TextColor.fromHexString("#969696"));
        return Component.text()
                .append(getPreviousButton())
                .append(divider)
                .append(getNextButton()).build();
    }
}