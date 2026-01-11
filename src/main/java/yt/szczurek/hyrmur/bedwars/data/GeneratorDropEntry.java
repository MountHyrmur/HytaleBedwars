package yt.szczurek.hyrmur.bedwars.data;

import java.util.List;

public class GeneratorDropEntry {
    public String item = ""; //TODO: Default item
    public int count = 1;
    public List<Float> cooldownsByLevel = List.of(1.0f);

    public String getItem() {
        return item;
    }

    public List<Float> getCooldownsByLevel() {
        return cooldownsByLevel;
    }

    public long getCooldown(int level) {
        float cooldownS = cooldownsByLevel.get(Integer.min(level, cooldownsByLevel.size() - 1));
        return (long) (cooldownS * 1000.0);
    }
}
