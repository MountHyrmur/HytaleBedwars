package yt.szczurek.hyrmur.bedwars.component

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.EntityUtil

object AutoNetworkId : Component<EntityStore?> {
    val CODEC: BuilderCodec<AutoNetworkId> =
        BuilderCodec.builder(AutoNetworkId::class.java) { AutoNetworkId }
            .build()
    val componentType: ComponentType<EntityStore, AutoNetworkId>
        get() = BedwarsPlugin.get().autoNetworkIdComponent

    override fun clone(): Component<EntityStore?> {
        return this
    }
}