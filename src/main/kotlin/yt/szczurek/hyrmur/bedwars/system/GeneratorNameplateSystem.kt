package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.Generator

class GeneratorNameplateSystem: DelayedEntitySystem<EntityStore>(1.0f) {
    val query = Generator.componentType
    override fun tick(
        dt: Float,
        i: Int,
        chunk: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>
    ) {
        val generator = chunk.getComponent(i, Generator.componentType)!!
        if (generator.cooldownsByItem.size != 1) {
            return
        }
        val cooldown = generator.cooldownsByItem.values.first()
        val cooldownSec = cooldown / 1000
        val nameplate = commandBuffer.ensureAndGetComponent(chunk.getReferenceTo(i), Nameplate.getComponentType())
        nameplate.text = "Spawning in $cooldownSec"
    }

    override fun getQuery(): Query<EntityStore> {
        return query
    }
}