package yt.szczurek.hyrmur.bedwars.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder;

public class AddNetworkIdToGeneratorSystem extends HolderSystem<EntityStore> {
    private static final ComponentType<EntityStore, GeneratorBuilder> GENERATOR_COMPONENT_TYPE = GeneratorBuilder.getComponentType();

    @Override
    public void onEntityAdd(@NotNull Holder<EntityStore> holder, @NotNull AddReason reason, @NotNull Store<EntityStore> store) {
        if (!holder.getArchetype().contains(NetworkId.getComponentType())) {
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        }
    }

    @Override
    public void onEntityRemoved(@NotNull Holder<EntityStore> var1, @NotNull RemoveReason var2, @NotNull Store<EntityStore> var3) {

    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return GENERATOR_COMPONENT_TYPE;
    }
}
