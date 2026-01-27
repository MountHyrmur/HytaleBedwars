package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.QueueSpawnpoint
import yt.szczurek.hyrmur.bedwars.component.TeamSpawnpoint
import java.awt.Color

class BedwarsMapValidateCommand :
    AbstractPlayerCommand("validate", "server.commands.bedwars.map.validate.desc") {
    override fun execute(
        ctx: CommandContext,
        store: Store<EntityStore>,
        ref: Ref<EntityStore>,
        playerRef: PlayerRef,
        world: World
    ) {
        val teamSpawnpointQuery = Query.and(TeamSpawnpoint.componentType)
        val teamSpawnpoints = store.getEntityCountFor(teamSpawnpointQuery)
        ctx.sendMessage(Message.raw("Found $teamSpawnpoints team spawnpoints"))
        if (teamSpawnpoints < 2) {
            ctx.sendMessage(Message.raw("Error: Map has less then 2 team spawnpoints").color(Color.RED))
        }

        val queueSpawnpointQuery = Query.and(QueueSpawnpoint.componentType)
        val queueSpawnpoints = store.getEntityCountFor(queueSpawnpointQuery)
        ctx.sendMessage(Message.raw("Found $queueSpawnpoints queue spawnpoints"))
        if (queueSpawnpoints == 0) {
            ctx.sendMessage(Message.raw("Error: Map needs at least one queue spawnpoint").color(Color.RED))
        }
    }
}
