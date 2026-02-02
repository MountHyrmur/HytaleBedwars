package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin

class BedwarsStartgameCommand: AbstractPlayerCommand("startgame", "server.commands.bedwars.map.startgame.desc")  {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore>,
        ref: Ref<EntityStore>,
        playerRef: PlayerRef,
        world: World
    ) {
        val transform = store.getComponent(ref, TransformComponent.getComponentType())!!
        val playerRef = store.getComponent(ref, PlayerRef.getComponentType())!!
        ctx.sendMessage(MESSAGE_START_START)
        BedwarsPlugin.get().scope.launch {
            val game = BedwarsPlugin.createGame("Forest", transform.transform, world).await()
            game.addPlayer(playerRef)
            ctx.sendMessage(MESSAGE_START_END)
        }
    }

    companion object {
        val MESSAGE_START_START = Message.translation("server.commands.bedwars.map.startgame.startStart")
        val MESSAGE_START_END = Message.translation("server.commands.bedwars.map.startgame.startEnd")
    }
}