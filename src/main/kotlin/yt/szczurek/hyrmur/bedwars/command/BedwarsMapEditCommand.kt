package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class BedwarsMapEditCommand : BedwarsMapActionCommand("edit") {
    override fun execute(
        commandContext: CommandContext,
        store: Store<EntityStore?>,
        ref: Ref<EntityStore?>,
        playerRef: PlayerRef,
        world: World
    ) {
    }
}