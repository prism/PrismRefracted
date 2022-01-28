package network.darkhelmet.prism.services.messages;

import com.google.inject.Inject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.moonshine.message.IMessageRenderer;

import network.darkhelmet.prism.services.translation.TranslationService;

import org.bukkit.command.CommandSender;

public class MessageRenderer implements IMessageRenderer<CommandSender, String, Component, Component> {
    /**
     * The translation service.
     */
    private final TranslationService translationService;

    /**
     * Contruct the message renderer.
     *
     * @param translationService The translation service
     */
    @Inject
    public MessageRenderer(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public Component render(
        final CommandSender receiver,
        final String intermediateMessage,
        final Map<String, ? extends Component> resolvedPlaceholders,
        final Method method,
        final Type owner
    ) {
        final List<Template> templates = new ArrayList<>();
        templates.add(Template.of("prefix", translationService.messageOf(receiver, "prefix")));

        for (final var entry : resolvedPlaceholders.entrySet()) {
            templates.add(Template.of(entry.getKey(), entry.getValue()));
        }

        return MiniMessage.get().parse(intermediateMessage, templates);
    }
}