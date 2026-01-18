package yt.szczurek.hyrmur.bedwars.component.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.EntityUtil;

import javax.annotation.Nonnull;

public class GeneratorBuilder implements Component<EntityStore> {
    private static final BuilderCodec<GeneratorBuilder> CODEC = BuilderCodec.builder(GeneratorBuilder.class, GeneratorBuilder::new)
            .append(new KeyedCodec<>("customField", Codec.STRING),
                    (data, value) -> data.generatorId = value,
                    data -> data.generatorId)
            .add()
            .build();

    String generatorId = "";

    @Nonnull
    public static ComponentType<EntityStore, GeneratorBuilder> getComponentType() {
        return BedwarsPlugin.get().getGeneratorBuilderComponent();
    }

    @Nonnull
    public static Holder<EntityStore> createGeneratorBuilderEntity(@Nonnull Vector3d pos, @Nonnull Store<EntityStore> store) {
        Holder<EntityStore> holder = EntityUtil.createUtilityEntity(pos, "Bedwars_Generator", store);
        holder.addComponent(getComponentType(), new GeneratorBuilder());
        return holder;
    }

    public GeneratorBuilder() {}

    public GeneratorBuilder(String generatorId) {
        this.generatorId = generatorId;
    }

    public String getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(String generatorId) {
        this.generatorId = generatorId;
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new GeneratorBuilder(this.generatorId);
    }
}
