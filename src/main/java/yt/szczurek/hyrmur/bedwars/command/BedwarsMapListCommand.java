package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.util.message.MessageFormat;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BedwarsMapListCommand extends AbstractAsyncCommand {
    public BedwarsMapListCommand() {
        super("list", "server.commands.bedwars.map.list.desc");
    }

    @Nonnull
    @Override
    public CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
//        List<String> instanceAssets = InstancesPlugin.get().getInstanceAssets();
//        context.sendMessage(
//                MessageFormat.list(Message.translation("server.commands.instances.edit.list.header"), instanceAssets.stream().map(Message::raw).toList())
//        );
        return CompletableFuture.completedFuture(null);
    }
}
