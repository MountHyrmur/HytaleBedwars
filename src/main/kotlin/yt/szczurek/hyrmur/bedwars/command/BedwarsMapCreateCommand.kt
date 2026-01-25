package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager.initializeWorld

class BedwarsMapCreateCommand : BedwarsMapActionCommand("create") {
    override fun execute(
        ctx: CommandContext, store: Store<EntityStore?>, ref: Ref<EntityStore?>, playerRef: PlayerRef, world: World
    ) {
        val mapName = mapNameArg.get(ctx)
        val error = BedwarsMapManager.createNew(mapName)
        if (error != null) {
            ctx.sendMessage(error)
            return
        }
        ctx.sendMessage(MESSAGE_CREATED.param("name", mapName!!))
        try {
            val mapWorld = BedwarsMapEditCommand.loadForEdit(mapName, playerRef)
            mapWorld.execute { initializeWorld(mapWorld) }
            world.execute {
                BedwarsMapManager.teleportPlayerToWorld(ref, mapWorld, store)
            }
        } catch (e: Exception) {
            ctx.sendMessage(Message.raw("Error: ${e.message}\n${e.stackTrace}"))
        }
    }

    companion object {
        val MESSAGE_CREATED = Message.translation("server.commands.bedwars.map.create.created")
    }
}
