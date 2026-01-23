package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager
import java.util.function.Consumer

class BedwarsMapCreateCommand : BedwarsMapActionCommand("create") {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore?>,
        ref: Ref<EntityStore?>,
        playerRef: PlayerRef,
        world: World
    ) {
        val mapName = mapNameArg.get(ctx)
        ctx.sendMessage(Message.translation("server.commands.bedwars.map.create.createStart").param("name", mapName!!))
        val error = BedwarsMapManager.createNewMap(mapName).join()
        if (error != null) {
            ctx.sendMessage(error)
            return
        }
        BedwarsMapManager.loadMapForEditing(mapName).thenAccept { mapWorld: World ->
            mapWorld.execute { BedwarsMapManager.initializeMapWorld(mapWorld) }
            ctx.sendMessage(
                Message.translation("server.commands.bedwars.map.create.createEnd").param("name", mapName)
            )
            world.execute {
                val spawnTransform = mapWorld.worldConfig.spawnProvider!!.getSpawnPoint(ref, store)
                val teleportComponent = Teleport.createForPlayer(mapWorld, spawnTransform)
                store.addComponent(ref, Teleport.getComponentType(), teleportComponent)
            }
        }
    }
}
