package yt.szczurek.hyrmur.bedwars.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GeneratorEditorGui extends InteractiveCustomUIPage<GeneratorEditorGui.BindingData> {

    @Nullable
    private String generatorName;
    private final Ref<EntityStore> generator;

    public GeneratorEditorGui(@Nonnull PlayerRef playerRef, @Nonnull Ref<EntityStore> generator, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, BindingData.CODEC);
        this.generator = generator;
    }

    public static class BindingData {
        public static final BuilderCodec<BindingData> CODEC = BuilderCodec.builder(BindingData.class, BindingData::new)
                .append(new KeyedCodec<>("@GeneratorName", Codec.STRING), (data, s) -> data.generatorName = s, data -> data.generatorName)
                .add()
                .build();

        public String generatorName;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/GeneratorEditorPage.ui");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#GeneratorName", EventData.of("@GeneratorName", "#GeneratorName.Value"), false);

        GeneratorBuilder generatorBuilder = store.getComponent(this.generator, GeneratorBuilder.getComponentType());
        assert generatorBuilder != null;
        uiCommandBuilder.set("#GeneratorName.Value", generatorBuilder.getGeneratorName());
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BindingData data) {
        super.handleDataEvent(ref, store, data);

        this.generatorName = data.generatorName;
    }

    @Override
    public void onDismiss(@NotNull Ref<EntityStore> ref, @NotNull Store<EntityStore> store) {
        super.onDismiss(ref, store);

        GeneratorBuilder generatorBuilder = store.getComponent(this.generator, GeneratorBuilder.getComponentType());
        assert generatorBuilder != null;
        if (generatorName != null && !generatorBuilder.getGeneratorName().equals(generatorName)) {
            generatorBuilder.setGeneratorName(generatorName);
        }
    }
}
