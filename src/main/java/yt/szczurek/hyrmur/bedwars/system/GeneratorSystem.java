package yt.szczurek.hyrmur.bedwars.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import yt.szczurek.hyrmur.bedwars.component.running.BedwarsGenerator;
import yt.szczurek.hyrmur.bedwars.data.GeneratorDropEntry;

import javax.annotation.Nonnull;

public class GeneratorSystem extends EntityTickingSystem<EntityStore> {
    private final ComponentType<EntityStore, BedwarsGenerator> generatorComponentType = BedwarsGenerator.getComponentType();

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        BedwarsGenerator generator = archetypeChunk.getComponent(index, generatorComponentType);
        assert generator != null;
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        for (var entry : generator.getCooldownsByItem().entrySet()) {
            if (entry.getValue() <= 0) {
                // Cooldown has passed
                GeneratorDropEntry drop = generator.getDrops().get(entry.getKey());
                ItemStack item = drop.getItemStack();
                ItemUtils.throwItem(ref, commandBuffer, item, Vector3d.ZERO,0.0f);
                entry.setValue(drop.getCooldown(generator.getLevel()));
            } else {
                long elapsed = (long) (dt * 1000.0);
                entry.setValue(entry.getValue() - elapsed);
            }
        }
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(this.generatorComponentType);
    }
}