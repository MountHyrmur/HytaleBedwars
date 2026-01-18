package yt.szczurek.hyrmur.bedwars.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import javax.annotation.Nonnull;

public class GeneratorDropEntry {
    @Nonnull
    public static BuilderCodec<GeneratorDropEntry> CODEC = BuilderCodec.builder(GeneratorDropEntry.class, GeneratorDropEntry::new)
            .append(new KeyedCodec<>("Item", Codec.STRING), (entry, key) -> entry.item = key, entry -> entry.item)
            .addValidatorLate(() -> Item.VALIDATOR_CACHE.getValidator().late())
            .add()
            .append(new KeyedCodec<>("Count", Codec.INTEGER), (entry, d) -> entry.count = d, entry -> entry.count)
            .add()
            .append(
                    new KeyedCodec<>("CooldownsByLevel", Codec.FLOAT_ARRAY),
                    (generator, l) -> generator.cooldownsByLevel = l,
                    generator -> generator.cooldownsByLevel
            )
            .add()
            .build();

    protected String item = "Ingredient_Bar_Iron";
    protected int count = 1;
    public float[] cooldownsByLevel = {1f};

    public String getItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return new ItemStack(item, count);
    }

    public float[] getCooldownsByLevel() {
        return cooldownsByLevel;
    }

    public long getCooldown(int level) {
        float cooldownS = cooldownsByLevel[(Integer.min(level, cooldownsByLevel.length - 1))];
        return (long) (cooldownS * 1000.0);
    }
}
