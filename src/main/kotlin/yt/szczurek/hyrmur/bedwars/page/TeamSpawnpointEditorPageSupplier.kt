package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.ComponentAccessor
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction.CustomPageSupplier
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore


class TeamSpawnpointEditorPageSupplier : CreativeEntityEditorPageSupplier() {
    override fun openPage(
        playerRef: PlayerRef,
        entity: Ref<EntityStore>
    ): CustomUIPage {
        return TeamSpawnpointEditorPage(playerRef, entity, CustomPageLifetime.CanDismissOrCloseThroughInteraction)
    }

    companion object {
        val CODEC: BuilderCodec<TeamSpawnpointEditorPageSupplier?> =
            BuilderCodec.builder(TeamSpawnpointEditorPageSupplier::class.java) { TeamSpawnpointEditorPageSupplier() }.build()
    }
}