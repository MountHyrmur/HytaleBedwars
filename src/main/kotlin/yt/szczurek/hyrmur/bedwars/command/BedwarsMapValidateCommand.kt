package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager
import yt.szczurek.hyrmur.bedwars.MESSAGE_NOT_BW_MAP

class BedwarsMapValidateCommand :
    AbstractPlayerCommand("validate", "server.commands.bedwars.map.validate.desc") {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore>,
        ref: Ref<EntityStore>,
        playerRef: PlayerRef,
        world: World
    ) {
        if (BedwarsMapManager.isABedwarsMapBeingEdited(world)) {
            val result = BedwarsMapManager.validateAndUpdateMetadata(world)
            ctx.sendMessage(result.toMessage())
        } else {
            ctx.sendMessage(MESSAGE_NOT_BW_MAP)
        }
    }
}
