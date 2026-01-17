package yt.szczurek.hyrmur.bedwars.component.running;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.data.GeneratorConfig;
import yt.szczurek.hyrmur.bedwars.data.GeneratorDropEntry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BedwarsGenerator implements Component<EntityStore> {
    private final Map<String, GeneratorDropEntry> drops;
    public int level = 0;
    private final HashMap<String, Long> cooldownsByItem;

    @Nonnull
    public static ComponentType<EntityStore, BedwarsGenerator> getComponentType() {
        return BedwarsPlugin.get().getBedwarsGeneratorComponentType();
    }

    public BedwarsGenerator(GeneratorConfig config) {
        this.drops = config.drops.stream().collect(Collectors.toMap(GeneratorDropEntry::getItem, Function.identity()));
        var cooldowns = new HashMap<String, Long>();
        for (var item: drops.keySet()) {
            cooldowns.put(item, 0L);
        }
        this.cooldownsByItem = cooldowns;
    }

    public BedwarsGenerator(BedwarsGenerator other) {
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
    public @NotNull BedwarsGenerator clone() {
        return new BedwarsGenerator(this);
    }
}
