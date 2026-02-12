package yt.szczurek.hyrmur.bedwars.event

import com.hypixel.hytale.component.system.EcsEvent
import com.hypixel.hytale.server.core.universe.PlayerRef
import java.util.UUID

class GroupPlayersEvent(val players: Collection<PlayerRef>): EcsEvent() {
    internal var groups: MutableList<Set<UUID>> = ArrayList()

    fun addGroup(group: Set<UUID>) {
        groups.add(group)
    }

    fun addGroup(group: Set<PlayerRef>) {
        addGroup(group.map(PlayerRef::getUuid).toSet())
    }
}