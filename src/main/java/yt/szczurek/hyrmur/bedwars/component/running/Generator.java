package yt.szczurek.hyrmur.bedwars.component.running;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.data.BedwarsGenerator;
import yt.szczurek.hyrmur.bedwars.data.GeneratorDropEntry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Generator implements Component<EntityStore> {
    private final Map<String, GeneratorDropEntry> drops;
    public int level = 0;
    private final HashMap<String, Long> cooldownsByItem;

    @Nonnull
    public static ComponentType<EntityStore, Generator> getComponentType() {
        return BedwarsPlugin.get().getBedwarsGeneratorComponentType();
    }

    @Nonnull
    public static Holder<EntityStore> createGeneratorEntity(@Nonnull Vector3d pos, @Nonnull Store<EntityStore> store) {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Bedwars_Generator");
        assert modelAsset != null;
        Model model = Model.createScaledModel(modelAsset, 1.0f);

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(pos, Vector3f.ZERO));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.ensureComponent(Intangible.getComponentType());
        Box boundingBox = model.getBoundingBox();
        assert boundingBox != null;
        holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(boundingBox));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.ensureComponent(UUIDComponent.getComponentType());
        return holder;
    }

    public Generator(BedwarsGenerator config) {
        this.drops = Arrays.stream(config.getDrops()).collect(Collectors.toMap(GeneratorDropEntry::getItem, Function.identity()));
        var cooldowns = new HashMap<String, Long>();
        for (var item: drops.keySet()) {
            cooldowns.put(item, 0L);
        }
        this.cooldownsByItem = cooldowns;
    }

    public Generator(Generator other) {
        this.drops = new HashMap<>(other.drops);
        this.level = other.level;
        this.cooldownsByItem = new HashMap<>(other.cooldownsByItem);
    }

    public Map<String, GeneratorDropEntry> getDrops() {
        return drops;
    }

    public int getLevel() {
        return level;
    }

    public HashMap<String, Long> getCooldownsByItem() {
        return cooldownsByItem;
    }

    @Override
    public @NotNull Generator clone() {
        return new Generator(this);
    }
}
