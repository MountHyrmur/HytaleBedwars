package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.component.Resource
import com.hypixel.hytale.component.ResourceType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class BedwarsGameHolder(var game: BedwarsGame? = null): Resource<EntityStore> {
    override fun clone(): Resource<EntityStore> {
        throw UnsupportedOperationException("Cloning a bedwars game doesn't make sense.")
    }

    companion object {
        val resourceType: ResourceType<EntityStore, BedwarsGameHolder>
            get() = BedwarsPlugin.get().bedwarsGameHolderResourceType
    }
}