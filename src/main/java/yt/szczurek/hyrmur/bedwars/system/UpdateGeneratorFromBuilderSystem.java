package yt.szczurek.hyrmur.bedwars.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder;
import yt.szczurek.hyrmur.bedwars.component.running.Generator;
import yt.szczurek.hyrmur.bedwars.data.BedwarsGenerator;

public class UpdateGeneratorFromBuilderSystem extends RefChangeSystem<EntityStore, GeneratorBuilder> {

    @Override
    public @NotNull ComponentType<EntityStore, GeneratorBuilder> componentType() {
        return GeneratorBuilder.getComponentType();
    }

    @Override
    public void onComponentAdded(@NotNull Ref<EntityStore> ref, @NotNull GeneratorBuilder builder, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {}

    @Override
    public void onComponentSet(@NotNull Ref<EntityStore> ref, @Nullable GeneratorBuilder oldBuilder, @NotNull GeneratorBuilder newBuilder, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {
        if (commandBuffer.getComponent(ref, Generator.getComponentType()) != null) {
            BedwarsGenerator config = BedwarsGenerator.getAssetMap().getAsset(newBuilder.getGeneratorName());
            assert config != null;
            commandBuffer.putComponent(ref, Generator.getComponentType(), new Generator(config));
        }
    }

    @Override
    public void onComponentRemoved(@NotNull Ref<EntityStore> ref, @NotNull GeneratorBuilder builder, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {}

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return GeneratorBuilder.getComponentType();
    }
}
