package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.launch
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.ExceptionWithMessage

class BedwarsMapEditCommand : BedwarsMapActionCommand("edit") {
    override fun execute(
        ctx: CommandContext, store: Store<EntityStore?>, ref: Ref<EntityStore?>, playerRef: PlayerRef, world: World
    ) {
        val mapName = mapNameArg.get(ctx)
        if (!BedwarsMapManager.doesMapExist(mapName)) {
            ctx.sendMessage(MESSAGE_NO_MAP.param("name", mapName))
            return
        }
        var mapWorld = BedwarsMapManager.getWorldLoadedForEditing(mapName)
        if (mapWorld != null) {
            BedwarsMapManager.teleportPlayerToWorld(ref, mapWorld, store)
            return
        }
        BedwarsPlugin.get().scope.launch {
            try {
                mapWorld = loadForEdit(mapName, playerRef)
            } catch (e: ExceptionWithMessage) {
                ctx.sendMessage(e.getMessage())
                return@launch
            }
            world.execute {
                BedwarsMapManager.teleportPlayerToWorld(ref, mapWorld, store)
            }
        }
    }

    companion object {
        val MESSAGE_NO_MAP = Message.translation("server.commands.bedwars.map.edit.fail.noMap")
        val MESSAGE_LOAD_START = Message.translation("server.commands.bedwars.map.common.loadStart")
        val MESSAGE_LOAD_END = Message.translation("server.commands.bedwars.map.common.loadEnd")

        suspend fun loadForEdit(name: String, player: PlayerRef): World {
            player.sendMessage(MESSAGE_LOAD_START.param("name", name))
            val mapWorld = BedwarsMapManager.loadForEditing(name)
            player.sendMessage(MESSAGE_LOAD_END.param("name", name))
            return mapWorld
        }
    }
}