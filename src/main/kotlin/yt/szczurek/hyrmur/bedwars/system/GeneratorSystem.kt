package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.tick.EntityTickingSystem
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.server.core.entity.ItemUtils
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.Generator
import yt.szczurek.hyrmur.bedwars.asset.data.GeneratorDropEntry
import javax.annotation.Nonnull

class GeneratorSystem : EntityTickingSystem<EntityStore?>() {
    private val generatorComponentType = Generator.componentType

    override fun tick(
        dt: Float, index: Int, archetypeChunk: ArchetypeChunk<EntityStore?>,
        store: Store<EntityStore?>, commandBuffer: CommandBuffer<EntityStore?>
    ) {
        val generator: Generator = checkNotNull(archetypeChunk.getComponent(index, generatorComponentType))
        val ref = archetypeChunk.getReferenceTo(index)

        for (entry in generator.cooldownsByItem.entries) {
            if (entry.value <= 0) {
                // Cooldown has passed
                val drop: GeneratorDropEntry = generator.drops[entry.key]!!
                val item = drop.itemStack
                ItemUtils.throwItem(ref, commandBuffer, item, Vector3d.ZERO, 0.0f)
                entry.setValue(drop.getCooldown(generator.level))
            } else {
                val elapsed = (dt * 1000.0).toLong()
                entry.setValue(entry.value - elapsed)
            }
        }
    }

    @Nonnull
    override fun getQuery(): Query<EntityStore?> {
        return this.generatorComponentType
    }
}