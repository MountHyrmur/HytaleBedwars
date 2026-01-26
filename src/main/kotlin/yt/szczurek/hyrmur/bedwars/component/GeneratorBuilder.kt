package yt.szczurek.hyrmur.bedwars.component

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.modules.entity.component.Interactable
import com.hypixel.hytale.server.core.modules.interaction.Interactions
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.EntityUtil

class GeneratorBuilder : Component<EntityStore?> {
    var generatorName: String = ""

    constructor()

    constructor(generatorName: String) {
        this.generatorName = generatorName
    }

    override fun clone(): Component<EntityStore?> {
        return GeneratorBuilder(this.generatorName)
    }

    companion object {
        val CODEC: BuilderCodec<GeneratorBuilder> =
            BuilderCodec.builder(GeneratorBuilder::class.java) { GeneratorBuilder() }.append(
                KeyedCodec("GeneratorName", Codec.STRING),
                    { data, value -> data.generatorName = value },
                    { data -> data.generatorName }).add().build()

        val componentType: ComponentType<EntityStore, GeneratorBuilder>
            get() = BedwarsPlugin.get().generatorBuilderComponent

        fun createGeneratorBuilderEntity(pos: Vector3d): Holder<EntityStore> {
            val holder: Holder<EntityStore> = EntityUtil.createUtilityEntity(pos, "Bedwars_Generator")
            holder.addComponent(componentType, GeneratorBuilder())
            holder.ensureComponent(Interactable.getComponentType())
            val interactions = Interactions()
            interactions.setInteractionId(InteractionType.Use, "OpenGeneratorEditor")
            holder.addComponent(Interactions.getComponentType(), interactions)
            return holder
        }
    }
}