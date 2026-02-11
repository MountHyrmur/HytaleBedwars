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

private val MESSAGE_CREATE_START = Message.translation("server.commands.bedwars.map.create.createStart")
private val MESSAGE_CREATE_END = Message.translation("server.commands.bedwars.map.create.createEnd")
private val MESSAGE_ALREADY_EXISTS = Message.translation("server.commands.bedwars.map.create.alreadyExists")

class BedwarsMapCreateCommand : BedwarsMapActionCommand("create") {
    override fun execute(
        ctx: CommandContext, store: Store<EntityStore?>, ref: Ref<EntityStore?>, playerRef: PlayerRef, world: World
    ) {
        val mapName = mapNameArg.get(ctx)
        if (BedwarsMapManager.doesMapExist(mapName)) {
            ctx.sendMessage(MESSAGE_ALREADY_EXISTS.param("name", mapName))
            return
        }
        ctx.sendMessage(MESSAGE_CREATE_START.param("name", mapName))
        try {
            BedwarsPlugin.get().scope.launch {
                BedwarsMapManager.createNew(mapName)
                ctx.sendMessage(MESSAGE_CREATE_END.param("name", mapName))
                val mapWorld = BedwarsMapEditCommand.loadForEdit(mapName, playerRef)
                mapWorld.execute { BedwarsMapManager.initializeWorld(mapWorld) }
                world.execute { BedwarsMapManager.teleportPlayerToWorld(ref, mapWorld, store) }
            }
        } catch (e: ExceptionWithMessage) {
            ctx.sendMessage(e.getMessage())
        }
    }
}
