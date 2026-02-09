package yt.szczurek.hyrmur.bedwars.component

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.modules.entity.component.Interactable
import com.hypixel.hytale.server.core.modules.interaction.Interactions
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.EntityUtil

class TeamSpawnpoint : Component<EntityStore?> {
    var team: String = ""

    constructor()

    constructor(team: String) {
        this.team = team
    }

    override fun clone(): Component<EntityStore?> {
        return TeamSpawnpoint(this.team)
    }

    companion object {
        val CODEC: BuilderCodec<TeamSpawnpoint> =
            BuilderCodec.builder(TeamSpawnpoint::class.java) { TeamSpawnpoint() }.append(
                KeyedCodec("Team", Codec.STRING),
                    { data, value -> data.team = value },
                    { data -> data.team })
                .add()
                .build()

        val componentType: ComponentType<EntityStore, TeamSpawnpoint>
            get() = BedwarsPlugin.get().teamSpawnpointComponent

        fun createTeamSpawnpoint(pos: Vector3d): Holder<EntityStore> {
            val holder: Holder<EntityStore> = EntityUtil.createUtilityEntity(pos, "Bedwars_Team_Spawnpoint")
            holder.addComponent(componentType, TeamSpawnpoint())
            holder.ensureComponent(Interactable.getComponentType())
            val interactions = Interactions()
            interactions.setInteractionId(InteractionType.Use, "OpenTeamSpawnpointEditor")
            holder.addComponent(Interactions.getComponentType(), interactions)
            return holder
        }
    }
}