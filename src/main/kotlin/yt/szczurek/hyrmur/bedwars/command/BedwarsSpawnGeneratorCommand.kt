package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.modules.entity.EntityModule
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder

class BedwarsSpawnGeneratorCommand :
    AbstractPlayerCommand("generator", "server.commands.bedwars.spawn.generator.desc") {
    override fun execute(
        commandContext: CommandContext,
        store: Store<EntityStore>,
        ref: Ref<EntityStore>,
        playerRef: PlayerRef,
        world: World
    ) {
        val playerTransform: TransformComponent =
            checkNotNull(store.getComponent(ref, EntityModule.get().transformComponentType))
        val pos = playerTransform.position.add(0.0, 1.0, 0.0)
        val generator = GeneratorBuilder.createGeneratorBuilderEntity(pos, store)

        world.execute { store.addEntity(generator, AddReason.SPAWN) }
    }
}
