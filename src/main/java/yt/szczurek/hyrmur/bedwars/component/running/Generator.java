package yt.szczurek.hyrmur.bedwars.component.running;

import yt.szczurek.hyrmur.bedwars.data.GeneratorConfig;
import yt.szczurek.hyrmur.bedwars.data.GeneratorDropEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Generator {
    private final Map<String, GeneratorDropEntry> drops;
    public int level = 0;
    private final HashMap<String, Long> cooldownsByItem;
    private long lastTick;

    public Generator(GeneratorConfig config) {
        this.drops = config.drops.stream().collect(Collectors.toMap(GeneratorDropEntry::getItem, Function.identity()));
        var cooldowns = new HashMap<String, Long>();
        for (var item: drops.keySet()) {
            cooldowns.put(item, 0L);
        }
        this.cooldownsByItem = cooldowns;
    }

    public void tick() {
        for (var entry: cooldownsByItem.entrySet()) {
            if (entry.getValue() <= 0) {
                // Cooldown has passed
                // TODO: Spawn item
                entry.setValue(drops.get(entry.getKey()).getCooldown(level));
            } else {
                long elapsed = System.currentTimeMillis() - lastTick;
                entry.setValue(entry.getValue() - elapsed);
            }
        }
        lastTick = System.currentTimeMillis();
    }
}
