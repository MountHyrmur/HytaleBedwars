package yt.szczurek.hyrmur.bedwars.data

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.inventory.ItemStack

class GeneratorDropEntry {
    var item: String = "Ingredient_Bar_Iron"
        private set
    private var count: Int = 1
    var cooldownsByLevel: FloatArray = floatArrayOf(1f)

    val itemStack: ItemStack
        get() = ItemStack(item, count)

    fun getCooldown(level: Int): Long {
        val cooldownS = cooldownsByLevel[(Integer.min(level, cooldownsByLevel.size - 1))]
        return (cooldownS * 1000.0).toLong()
    }

    companion object {
        @JvmField
        val CODEC: BuilderCodec<GeneratorDropEntry> =
            BuilderCodec.builder(GeneratorDropEntry::class.java) { GeneratorDropEntry() }
                .append(
                    KeyedCodec("Item", Codec.STRING),
                    { entry, key: String -> entry.item = key },
                    { entry -> entry.item })
                .addValidatorLate { Item.VALIDATOR_CACHE.getValidator().late() }
                .add()
                .append(
                    KeyedCodec("Count", Codec.INTEGER),
                    { entry, d: Int -> entry.count = d },
                    { entry -> entry.count })
                .add()
                .append(
                    KeyedCodec("CooldownsByLevel", Codec.FLOAT_ARRAY),
                    { generator, l: FloatArray -> generator.cooldownsByLevel = l },
                    { generator -> generator.cooldownsByLevel }
                )
                .add()
                .build()
    }
}
