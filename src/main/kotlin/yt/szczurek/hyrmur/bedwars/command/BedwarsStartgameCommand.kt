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
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGameConfig

private val MESSAGE_START_START = Message.translation("server.commands.bedwars.map.startgame.startStart")
private val MESSAGE_START_END = Message.translation("server.commands.bedwars.map.startgame.startEnd")

class BedwarsStartgameCommand: AbstractPlayerCommand("startgame", "server.commands.bedwars.map.startgame.desc")  {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore>,
        ref: Ref<EntityStore>,
        playerRef: PlayerRef,
        world: World
    ) {
        val transform = store.getComponent(ref, TransformComponent.getComponentType())!!.transform.clone()
        ctx.sendMessage(MESSAGE_START_START)
        val config = BedwarsGameConfig(1)
        BedwarsPlugin.get().scope.launch {
            val game = BedwarsPlugin.createGame("Forest", config, transform, world).await()
            world.execute {
                game.addPlayer(ref, store)
            }
            ctx.sendMessage(MESSAGE_START_END)
        }
    }
}