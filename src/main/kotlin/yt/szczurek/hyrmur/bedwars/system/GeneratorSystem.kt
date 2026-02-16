package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.tick.EntityTickingSystem
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
import com.hypixel.hytale.server.core.modules.entity.item.PreventItemMerging
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.asset.data.GeneratorDropEntry
import yt.szczurek.hyrmur.bedwars.component.Generator

class GeneratorSystem : EntityTickingSystem<EntityStore>() {
    private val generatorComponentType = Generator.componentType

    override fun tick(
        dt: Float, index: Int, archetypeChunk: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>, commandBuffer: CommandBuffer<EntityStore>
    ) {
        val generator: Generator = checkNotNull(archetypeChunk.getComponent(index, generatorComponentType))
        val ref = archetypeChunk.getReferenceTo(index)

        for (entry in generator.cooldownsByItem.entries) {
            if (entry.value <= 0) {
                // Cooldown has passed
                val drop: GeneratorDropEntry = generator.drops[entry.key]!!
                val transform = commandBuffer.getComponent(ref, TransformComponent.getComponentType())!!
                repeat(drop.count) {
                    spawnItem(drop.item, transform.position, commandBuffer)
                }
                entry.setValue(drop.getCooldown(generator.level))
            } else {
                val elapsed = (dt * 1000.0).toLong()
                entry.setValue(entry.value - elapsed)
            }
        }
    }

    fun spawnItem(item: String, pos: Vector3d, commandBuffer: CommandBuffer<EntityStore>) {
        val holder = ItemComponent.generateItemDrop(
            commandBuffer,
            ItemStack(item),
            pos,
            Vector3f.UP.rotateY((Math.random() * 2 * Math.PI).toFloat()),
            0.0f,
            0.0f,
            0.0f
        )
        holder ?: return

        val itemComponent = holder.getComponent(ItemComponent.getComponentType())
        itemComponent?.setPickupDelay(1.5f)
        holder.ensureComponent( PreventItemMerging.getComponentType())

        commandBuffer.addEntity(holder, AddReason.SPAWN)
    }

    override fun getQuery(): Query<EntityStore> {
        return this.generatorComponentType
    }
}