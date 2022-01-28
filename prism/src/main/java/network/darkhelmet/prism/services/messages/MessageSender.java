package network.darkhelmet.prism.services.messages;

import com.google.inject.Inject;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.message.IMessageSender;

import org.bukkit.command.CommandSender;

public class MessageSender implements IMessageSender<CommandSender, Component> {
    private final BukkitAudiences audiences;

    @Inject
    public MessageSender(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public void send(final CommandSender receiver, final Component renderedMessage) {
        audiences.sender(receiver).sendMessage(Identity.nil(), renderedMessage, MessageType.SYSTEM);
    }
}