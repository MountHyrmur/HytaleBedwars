package yt.szczurek.hyrmur.bedwars.interaction

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.EntityUtil


class SnapToGridInteraction : SimpleInstantInteraction() {
    override fun firstRun(
        type: InteractionType,
        ctx: InteractionContext,
        cooldownHandler: CooldownHandler
    ) {
        val entity = ctx.targetEntity

        if (entity == null) {
            ctx.state.state = InteractionState.Failed
            return
        }

        val commandBuffer: CommandBuffer<EntityStore?> = checkNotNull(ctx.commandBuffer)

        EntityUtil.snapEntityToGrid(entity, commandBuffer)
    }

    companion object {
        val CODEC: BuilderCodec<SnapToGridInteraction> = BuilderCodec.builder(
            SnapToGridInteraction::class.java,
            { SnapToGridInteraction() },
            SimpleInstantInteraction.CODEC
        ).build()
    }
}