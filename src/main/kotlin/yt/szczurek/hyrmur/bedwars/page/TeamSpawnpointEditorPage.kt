package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGenerator
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam
import yt.szczurek.hyrmur.bedwars.component.Generator
import yt.szczurek.hyrmur.bedwars.component.GeneratorBuilder
import yt.szczurek.hyrmur.bedwars.component.TeamSpawnpoint

class TeamSpawnpointEditorPage(
    playerRef: PlayerRef,
    private val spawnpoint: Ref<EntityStore>,
    lifetime: CustomPageLifetime
) : InteractiveCustomUIPage<TeamSpawnpointEditorPage.BindingData>(playerRef, lifetime, BindingData.CODEC) {
    private var teamName: String? = null

    class BindingData {
        var teamName: String? = null

        companion object {
            val CODEC: BuilderCodec<BindingData> =
                BuilderCodec.builder(BindingData::class.java) { BindingData() }
                    .append(
                        KeyedCodec("@TeamName", Codec.STRING),
                        { data, s -> data.teamName = s },
                        { data -> data.teamName })
                    .add()
                    .build()
        }
    }

    override fun build(ref: Ref<EntityStore>, uiCommandBuilder: UICommandBuilder, uiEventBuilder: UIEventBuilder, store: Store<EntityStore>) {
        uiCommandBuilder.append("Pages/TeamSpawnpointEditorPage.ui")

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            "#TeamName",
            EventData.of("@TeamName", "#TeamName.Value"),
            false
        )

        val spawnpointComponent: TeamSpawnpoint =
            checkNotNull(store.getComponent(spawnpoint, TeamSpawnpoint.componentType))
        teamName = spawnpointComponent.team
        uiCommandBuilder.set("#TeamName.Value", teamName!!)
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: BindingData
    ) {
        super.handleDataEvent(ref, store, data)

        if (data.teamName != null) {
            val newName = data.teamName!!.trim { it <= ' ' }
            if (newName != teamName) {
                teamName = newName
                onGeneratorNameChange(store)
            }
        }
    }

    private fun onGeneratorNameChange(store: Store<EntityStore>) {
        // Return on invalid name
        team ?: return
        store.putComponent(
            spawnpoint,
            TeamSpawnpoint.componentType,
            TeamSpawnpoint(teamName!!)
        )
    }

    private val team: BedwarsTeam?
        get() = BedwarsTeam.assetMap.getAsset(teamName)
}