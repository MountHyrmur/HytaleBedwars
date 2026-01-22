package yt.szczurek.hyrmur.bedwars.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder;
import yt.szczurek.hyrmur.bedwars.component.running.Generator;
import yt.szczurek.hyrmur.bedwars.data.BedwarsGenerator;

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
                .append(new KeyedCodec<>("Action", Codec.STRING), (data, s) -> data.action = s, data -> data.action)
                .add()
                .build();

        public String generatorName;
        public String action;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/GeneratorEditorPage.ui");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#GeneratorName", EventData.of("@GeneratorName", "#GeneratorName.Value"), false);

        GeneratorBuilder generatorBuilder = store.getComponent(generator, GeneratorBuilder.getComponentType());
        assert generatorBuilder != null;
        generatorName = generatorBuilder.getGeneratorName();
        uiCommandBuilder.set("#GeneratorName.Value", generatorName);

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ActivateGenerator", new EventData().append("Action", "ActivateGenerator"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#DeactivateGenerator", new EventData().append("Action", "DeactivateGenerator"), false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BindingData data) {
        super.handleDataEvent(ref, store, data);

        if (data.generatorName != null) {
            String newName = data.generatorName.trim();
            if (!newName.equals(generatorName)) {
                generatorName = newName;
                onGeneratorNameChange(store);
            }
        }

        if (data.action != null) {
            handleAction(data.action, store);
        }
    }

    private void onGeneratorNameChange(@Nonnull Store<EntityStore> store) {
        BedwarsGenerator config = getGeneratorConfig();
        if (config == null) {
            // Invalid name
            return;
        }
        store.putComponent(generator, GeneratorBuilder.getComponentType(), new GeneratorBuilder(generatorName));
    }

    private void handleAction(@Nonnull String action, @Nonnull Store<EntityStore> store) {
        switch (action) {
            case "ActivateGenerator":
                Generator component = store.getComponent(generator, Generator.getComponentType());
                if (component != null) {
                    break;
                }

                BedwarsGenerator config = getGeneratorConfig();
                if (config == null) {
                    playerRef.sendMessage(Message.translation("server.customUI.generatorEditor.error.invalidGeneratorName"));
                    break;
                }

                store.addComponent(generator, Generator.getComponentType(), new Generator(config));

                break;
            case "DeactivateGenerator":
                store.removeComponentIfExists(generator, Generator.getComponentType());
                return;
            default:
                return;
        }
        close();
    }

    private @Nullable BedwarsGenerator getGeneratorConfig() {
        return BedwarsGenerator.getAssetMap().getAsset(generatorName);
    }
}
