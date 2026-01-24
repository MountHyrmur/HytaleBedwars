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

class BedwarsMapExitCommand : AbstractPlayerCommand("exit", "server.commands.bedwars.map.exit.desc") {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore?>,
        ref: Ref<EntityStore?>,
        player: PlayerRef,
        world: World
    ) {
        try {
            InstancesPlugin.exitInstance(ref, store)
            ctx.sendMessage(MESSAGE_EXIT_SUCCESS)
        } catch (_: IllegalArgumentException) {
            ctx.sendMessage(MESSAGE_EXIT_FAIL)
        }
    }
    companion object {
        val MESSAGE_EXIT_SUCCESS = Message.translation("server.commands.bedwars.map.exit.success")
        val MESSAGE_EXIT_FAIL = Message.translation("server.commands.bedwars.map.exit.fail")
    }
}
