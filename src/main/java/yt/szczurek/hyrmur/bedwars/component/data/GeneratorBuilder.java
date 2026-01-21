package yt.szczurek.hyrmur.bedwars.component.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.EntityUtil;

import javax.annotation.Nonnull;

public class GeneratorBuilder implements Component<EntityStore> {
    public static final BuilderCodec<GeneratorBuilder> CODEC = BuilderCodec.builder(GeneratorBuilder.class, GeneratorBuilder::new)
            .append(new KeyedCodec<>("GeneratorName", Codec.STRING),
                    (data, value) -> data.generatorName = value,
                    data -> data.generatorName)
            .add()
            .build();

    String generatorName = "";

    @Nonnull
    public static ComponentType<EntityStore, GeneratorBuilder> getComponentType() {
        return BedwarsPlugin.get().getGeneratorBuilderComponent();
    }

    @Nonnull
    public static Holder<EntityStore> createGeneratorBuilderEntity(@Nonnull Vector3d pos, @Nonnull Store<EntityStore> store) {
        Holder<EntityStore> holder = EntityUtil.createUtilityEntity(pos, "Bedwars_Generator", store);
        holder.addComponent(getComponentType(), new GeneratorBuilder());
        holder.ensureComponent(Interactable.getComponentType());
        Interactions interactions = new Interactions();
        interactions.setInteractionId(InteractionType.Use, "OpenGeneratorEditor");
        holder.addComponent(Interactions.getComponentType(), interactions);
        return holder;
    }

    public GeneratorBuilder() {}

    public GeneratorBuilder(String generatorName) {
        this.generatorName = generatorName;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new GeneratorBuilder(this.generatorName);
    }
}
