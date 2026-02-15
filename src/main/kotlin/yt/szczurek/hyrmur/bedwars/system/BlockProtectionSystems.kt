package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsGame

object BlockProtectionSystems {
    val query: Query<EntityStore> = Player.getComponentType()

    fun shouldCancel(
        i: Int,
        archetypeChunk: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
    ): Boolean {
        // Cancel all breaks in bw game for now
        return BedwarsGame.get(store) != null
    }

    class BlockDamageSystem() : EntityEventSystem<EntityStore, DamageBlockEvent>(DamageBlockEvent::class.java) {
        override fun handle(
            i: Int,
            archetypeChunk: ArchetypeChunk<EntityStore>,
            store: Store<EntityStore>,
            commandBuffer: CommandBuffer<EntityStore>,
            event: DamageBlockEvent
        ) {
            if (shouldCancel(i, archetypeChunk, store, commandBuffer)) {
                event.isCancelled = true
            }
        }

        override fun getQuery(): Query<EntityStore> {
            return BlockProtectionSystems.query
        }
    }

    class BlockBreakSystem() : EntityEventSystem<EntityStore, BreakBlockEvent>(BreakBlockEvent::class.java) {
        override fun handle(
            i: Int,
            archetypeChunk: ArchetypeChunk<EntityStore>,
            store: Store<EntityStore>,
            commandBuffer: CommandBuffer<EntityStore>,
            event: BreakBlockEvent
        ) {
            if (shouldCancel(i, archetypeChunk, store, commandBuffer)) {
                event.isCancelled = true
            }
        }

        override fun getQuery(): Query<EntityStore> {
            return BlockProtectionSystems.query
        }
    }
}