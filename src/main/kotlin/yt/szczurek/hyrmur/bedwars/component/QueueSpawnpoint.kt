package yt.szczurek.hyrmur.bedwars.component

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.EntityUtil

object QueueSpawnpoint : Component<EntityStore?> {
    val CODEC: BuilderCodec<QueueSpawnpoint> =
        BuilderCodec.builder(QueueSpawnpoint::class.java) { QueueSpawnpoint }.build()
    val componentType: ComponentType<EntityStore, QueueSpawnpoint>
        get() = BedwarsPlugin.get().queueSpawnpointComponent
    val query: Query<EntityStore?>
        get() = Query.and(componentType)

    override fun clone(): Component<EntityStore?> {
        return this
    }

    fun createQueueSpawnpoint(pos: Vector3d): Holder<EntityStore> {
        val holder: Holder<EntityStore> = EntityUtil.createUtilityEntity(pos, "Bedwars_Queue_Spawnpoint")
        holder.addComponent(componentType, QueueSpawnpoint)
//            holder.ensureComponent(Interactable.getComponentType())
//            val interactions = Interactions()
        //  TODO:
//            interactions.setInteractionId(InteractionType.Use, "OpenGeneratorEditor")
//            holder.addComponent(Interactions.getComponentType(), interactions)
        return holder
    }

}