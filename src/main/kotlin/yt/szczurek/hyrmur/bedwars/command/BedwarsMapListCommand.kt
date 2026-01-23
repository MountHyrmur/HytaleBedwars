package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import java.util.concurrent.CompletableFuture
import javax.annotation.Nonnull

class BedwarsMapListCommand : AbstractAsyncCommand("list", "server.commands.bedwars.map.list.desc") {
    @Nonnull
    public override fun executeAsync(context: CommandContext): CompletableFuture<Void?> {
//        List<String> instanceAssets = InstancesPlugin.get().getInstanceAssets();
//        context.sendMessage(
//                MessageFormat.list(Message.translation("server.commands.instances.edit.list.header"), instanceAssets.stream().map(Message::raw).toList())
//        );
        return CompletableFuture.completedFuture<Void?>(null)
    }
}
