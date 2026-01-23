package yt.szczurek.hyrmur.bedwars.interaction

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.ui.GeneratorEditorGui


class EditGeneratorInteraction : SimpleInstantInteraction() {
    override fun firstRun(
        type: InteractionType,
        ctx: InteractionContext,
        cooldownHandler: CooldownHandler
    ) {
        val generator = ctx.targetEntity

        if (generator == null) {
            ctx.state.state = InteractionState.Failed
            return
        }

        val commandBuffer: CommandBuffer<EntityStore?> = checkNotNull(ctx.commandBuffer)
        val player = ctx.entity
        val playerComponent = commandBuffer.getComponent(player, Player.getComponentType())
        if (playerComponent == null) {
            ctx.state.state = InteractionState.Failed
            return
        }
        val playerRef: PlayerRef =
            checkNotNull(commandBuffer.getComponent(player, PlayerRef.getComponentType()))
        if (!playerComponent.hasPermission("Creative")) {
            ctx.state.state = InteractionState.Failed
            playerRef.sendMessage(
                Message.translation(
                    "server.commands.parsing.error.noPermissionForCommand"
                )
            )
            return
        }


        val gui = GeneratorEditorGui(playerRef, generator, CustomPageLifetime.CanDismissOrCloseThroughInteraction)
        playerComponent.pageManager.openCustomPage(player, player.getStore(), gui)
    }

    companion object {
        val CODEC: BuilderCodec<EditGeneratorInteraction> = BuilderCodec.builder(
            EditGeneratorInteraction::class.java,
            { EditGeneratorInteraction() },
            SimpleInstantInteraction.CODEC
        ).build()
    }
}