package network.darkhelmet.prism.services.messages;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import net.kyori.moonshine.receiver.IReceiverLocator;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ReceiverResolver implements IReceiverLocatorResolver<CommandSender> {
    @Override
    public IReceiverLocator<CommandSender> resolve(final Method method, final Type proxy) {
        return new Resolver();
    }

    private static final class Resolver implements IReceiverLocator<CommandSender> {
        @Override
        public CommandSender locate(final Method method, final Object proxy, final @Nullable Object[] parameters) {
            if (parameters.length == 0) {
                return null;
            }

            final Object parameter = parameters[0];
            if (parameter instanceof CommandSender sender) {
                return sender;
            }

            return null;
        }
    }
}