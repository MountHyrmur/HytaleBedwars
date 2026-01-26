package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import com.hypixel.hytale.server.core.util.message.MessageFormat
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap
import java.util.concurrent.CompletableFuture

class BedwarsMapListCommand : AbstractAsyncCommand("list", "server.commands.bedwars.map.list.desc") {

    override fun executeAsync(ctx: CommandContext): CompletableFuture<Void> {
        val mapAssets = BedwarsMap.assetMap.assetMap.keys
        ctx.sendMessage(
            MessageFormat.list(HEADER, mapAssets.stream().map(Message::raw).toList())
        )
        return CompletableFuture.completedFuture(null)
    }

    companion object {
        val HEADER = Message.translation("server.commands.bedwars.map.list.header")
    }
}
