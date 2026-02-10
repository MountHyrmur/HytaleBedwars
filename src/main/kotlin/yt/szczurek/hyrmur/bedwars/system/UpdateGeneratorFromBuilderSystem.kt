package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.RefChangeSystem
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.GeneratorBuilder
import yt.szczurek.hyrmur.bedwars.component.Generator
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGenerator

class UpdateGeneratorFromBuilderSystem : RefChangeSystem<EntityStore, GeneratorBuilder>() {
    private val generatorComponentType = GeneratorBuilder.componentType

    override fun componentType(): ComponentType<EntityStore, GeneratorBuilder> {
        return generatorComponentType
    }

    override fun onComponentAdded(
        ref: Ref<EntityStore?>,
        builder: GeneratorBuilder,
        store: Store<EntityStore?>,
        commandBuffer: CommandBuffer<EntityStore?>
    ) {
    }

    override fun onComponentSet(
        ref: Ref<EntityStore?>,
        oldBuilder: GeneratorBuilder?,
        newBuilder: GeneratorBuilder,
        store: Store<EntityStore?>,
        commandBuffer: CommandBuffer<EntityStore?>
    ) {
        if (commandBuffer.getComponent(ref, Generator.componentType) != null) {
            val config: BedwarsGenerator =
                checkNotNull(BedwarsGenerator.assetMap.getAsset(newBuilder.generatorName))
            commandBuffer.putComponent(ref, Generator.componentType, Generator(config))
        }
    }

    override fun onComponentRemoved(
        ref: Ref<EntityStore?>,
        builder: GeneratorBuilder,
        store: Store<EntityStore?>,
        commandBuffer: CommandBuffer<EntityStore?>
    ) {
    }

    override fun getQuery(): Query<EntityStore?> {
        return generatorComponentType
    }
}
