package yt.szczurek.hyrmur.bedwars.data;

import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.List;

public class GeneratorDropEntry {
    private String item = "Ingredient_Bar_Iron";
    private int count = 1;
    public List<Float> cooldownsByLevel = List.of(1.0f);

    public String getItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return new ItemStack(item, count);
    }

    public List<Float> getCooldownsByLevel() {
        return cooldownsByLevel;
    }

    public long getCooldown(int level) {
        float cooldownS = cooldownsByLevel.get(Integer.min(level, cooldownsByLevel.size() - 1));
        return (long) (cooldownS * 1000.0);
    }
}
