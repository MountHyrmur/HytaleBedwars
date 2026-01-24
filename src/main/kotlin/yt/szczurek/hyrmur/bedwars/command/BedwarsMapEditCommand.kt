package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager.loadForEditing
import java.util.concurrent.CompletableFuture

class BedwarsMapEditCommand : BedwarsMapActionCommand("edit") {
    override fun execute(
        ctx: CommandContext, store: Store<EntityStore?>, ref: Ref<EntityStore?>, playerRef: PlayerRef, world: World
    ) {
        val mapName = mapNameArg.get(ctx)
        if (!BedwarsMapManager.doesMapExist(mapName)) {
            ctx.sendMessage(MESSAGE_NOT_EXIST.param("name", mapName))
            return
        }
        var mapWorld = BedwarsMapManager.getWorldLoadedForEditing(mapName)
        mapWorld = mapWorld ?: loadForEdit(mapName, playerRef).join()
        BedwarsMapManager.teleportPlayerToWorld(playerRef, world, mapWorld, store)
    }

    companion object {
        val MESSAGE_NOT_EXIST = Message.translation("server.commands.bedwars.map.edit.notExist")
        val MESSAGE_LOAD_START = Message.translation("server.commands.bedwars.map.common.loadStart")
        val MESSAGE_LOAD_END = Message.translation("server.commands.bedwars.map.common.loadEnd")

        fun loadForEdit(name: String, player: PlayerRef): CompletableFuture<World> {
            player.sendMessage(MESSAGE_LOAD_START.param("name", name))
            return loadForEditing(name).thenApply { mapWorld: World ->
                player.sendMessage(MESSAGE_LOAD_END.param("name", name))
                return@thenApply mapWorld
            }
        }
    }
}