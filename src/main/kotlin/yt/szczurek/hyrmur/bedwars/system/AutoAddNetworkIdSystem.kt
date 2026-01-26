package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.RemoveReason
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.HolderSystem
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.AutoNetworkId

class AutoAddNetworkIdSystem : HolderSystem<EntityStore?>() {
    private val autoNetworkIdComponentType = AutoNetworkId.componentType
    private val networkIdComponentType = NetworkId.getComponentType()
    private val query: Query<EntityStore?> =
        Query.and(this.autoNetworkIdComponentType, Query.not(this.networkIdComponentType))

    override fun onEntityAdd(holder: Holder<EntityStore?>, reason: AddReason, store: Store<EntityStore?>) {
        if (!holder.getArchetype()!!.contains(NetworkId.getComponentType())) {
            holder.addComponent(
                NetworkId.getComponentType(),
                NetworkId(store.getExternalData().takeNextNetworkId())
            )
        }
    }

    override fun onEntityRemoved(var1: Holder<EntityStore?>, var2: RemoveReason, var3: Store<EntityStore?>) {
    }

    override fun getQuery(): Query<EntityStore?> {
        return query
    }
}
