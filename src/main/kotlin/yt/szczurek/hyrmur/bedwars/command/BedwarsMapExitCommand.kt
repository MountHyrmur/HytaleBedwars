package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager

class BedwarsMapExitCommand : AbstractPlayerCommand("exit", "server.commands.bedwars.map.exit.desc") {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore?>,
        ref: Ref<EntityStore?>,
        player: PlayerRef,
        world: World
    ) {
        if (!BedwarsMapManager.isABedwarsMapBeingEdited(world)) {
            ctx.sendMessage(BedwarsMapManager.MESSAGE_NOT_BW_MAP)
            return
        }

        val result = BedwarsMapManager.validateAndUpdateMetadata(world)

        try {
            InstancesPlugin.exitInstance(ref, store)
            ctx.sendMessage(MESSAGE_EXIT_SUCCESS)
            if (!result.isOk()) {
                ctx.sendMessage(MESSAGE_WARN_NOT_VALID)
            }
        } catch (_: IllegalArgumentException) {
            ctx.sendMessage(BedwarsMapManager.MESSAGE_NOT_BW_MAP)
        }
    }
    companion object {
        val MESSAGE_EXIT_SUCCESS = Message.translation("server.commands.bedwars.map.exit.success")
        val MESSAGE_WARN_NOT_VALID = Message.translation("server.commands.bedwars.map.exit.warnNotValid")
    }
}
