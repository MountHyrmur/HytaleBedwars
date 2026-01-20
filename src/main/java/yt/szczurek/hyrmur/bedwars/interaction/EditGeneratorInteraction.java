package yt.szczurek.hyrmur.bedwars.interaction;


import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.ui.GeneratorEditorGui;

import javax.annotation.Nonnull;

public class EditGeneratorInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<EditGeneratorInteraction> CODEC = BuilderCodec.builder(
            EditGeneratorInteraction.class, EditGeneratorInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext ctx, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> generator = ctx.getTargetEntity();

        if (generator == null) {
            ctx.getState().state = InteractionState.Failed;
            return;
        }

        CommandBuffer<EntityStore> commandBuffer = ctx.getCommandBuffer();
        assert commandBuffer != null;

        Ref<EntityStore> player = ctx.getEntity();
        Player playerComponent = commandBuffer.getComponent(player, Player.getComponentType());
        if (playerComponent == null) {
            ctx.getState().state = InteractionState.Failed;
            return;
        }
        PlayerRef playerRef= commandBuffer.getComponent(player, PlayerRef.getComponentType());
        assert  playerRef != null;

        GeneratorEditorGui gui = new GeneratorEditorGui(playerRef, generator, CustomPageLifetime.CanDismissOrCloseThroughInteraction);
        playerComponent.getPageManager().openCustomPage(player, player.getStore(), gui);
    }
}