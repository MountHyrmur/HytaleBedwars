package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.ComponentAccessor
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction.CustomPageSupplier
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore


abstract class CreativeEntityEditorPageSupplier : CustomPageSupplier {
    override fun tryCreate(
        ref: Ref<EntityStore?>,
        componentAccessor: ComponentAccessor<EntityStore?>,
        playerRef: PlayerRef,
        ctx: InteractionContext
    ): CustomUIPage? {
        val entity = ctx.targetEntity

        if (entity == null) {
            ctx.state.state = InteractionState.Failed
            return null
        }

        val commandBuffer: CommandBuffer<EntityStore?> = checkNotNull(ctx.commandBuffer)
        val player = ctx.entity
        val playerComponent = commandBuffer.getComponent(player, Player.getComponentType())
        if (playerComponent == null) {
            ctx.state.state = InteractionState.Failed
            return null
        }
        val playerRef: PlayerRef = checkNotNull(commandBuffer.getComponent(player, PlayerRef.getComponentType()))
        if (playerComponent.gameMode != GameMode.Creative) {
            ctx.state.state = InteractionState.Failed
            playerRef.sendMessage(
                Message.translation(
                    "server.commands.parsing.error.noPermissionForCommand"
                )
            )
            return null
        }

        return openPage(playerRef, entity)
    }

    abstract fun openPage(playerRef: PlayerRef, entity: Ref<EntityStore>): CustomUIPage?
}