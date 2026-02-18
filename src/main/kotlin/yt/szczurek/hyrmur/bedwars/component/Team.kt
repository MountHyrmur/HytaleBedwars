package yt.szczurek.hyrmur.bedwars.component

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam

class Team(val team: BedwarsTeam) : Component<EntityStore?> {

    override fun clone(): Component<EntityStore?> {
        return Team(this.team)
    }

    companion object {
        val componentType: ComponentType<EntityStore, Team>
            get() = BedwarsPlugin.get().teamComponentType
    }
}