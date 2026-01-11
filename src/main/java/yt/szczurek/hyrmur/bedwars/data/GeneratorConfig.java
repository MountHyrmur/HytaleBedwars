package yt.szczurek.hyrmur.bedwars.data;

import java.util.List;

public class GeneratorConfig {
    public String id;
    public List<GeneratorDropEntry> drops;

    public int maxLevel() {
        return drops.stream().map(entry -> entry.cooldownsByLevel.size()).min(Integer::compare).orElseThrow() - 1;
    }
}
