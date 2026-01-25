package yt.szczurek.hyrmur.bedwars.asset

import com.hypixel.hytale.assetstore.AssetExtraInfo
import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.codec.schema.metadata.ui.UIDefaultCollapsedState
import yt.szczurek.hyrmur.bedwars.asset.data.GeneratorDropEntry
import java.util.*

class BedwarsGenerator : JsonAssetWithMap<String?, DefaultAssetMap<String?, BedwarsGenerator?>?> {
    var name: String? = null
        private set
    private var data: AssetExtraInfo.Data? = null
    var drops: Array<GeneratorDropEntry>? = null
        private set

    override fun getId(): String? {
        return name
    }

    fun maxLevel(): Int {
        return Arrays.stream(drops).map { entry -> entry.cooldownsByLevel.size }.min(Int::compareTo).orElseThrow() - 1
    }

    companion object {
        val CODEC: AssetBuilderCodec<String?, BedwarsGenerator> =
            AssetBuilderCodec.builder(
                BedwarsGenerator::class.java,
                { BedwarsGenerator() },
                Codec.STRING,
                { generator, id -> generator.name = id },
                { generator -> generator.name },
                { asset, data -> asset.data = data },
                { asset -> asset.data }).append(
                KeyedCodec(
                    "Drops", ArrayCodec(GeneratorDropEntry.CODEC) { size -> arrayOfNulls(size) }),
                    { generator, l -> generator.drops = l },
                    { generator -> generator.drops }).metadata(UIDefaultCollapsedState.UNCOLLAPSED).add().build()


        private var ASSET_MAP: DefaultAssetMap<String?, BedwarsGenerator?>? = null
        val assetMap: DefaultAssetMap<String?, BedwarsGenerator?>
            get() {
                if (ASSET_MAP == null) {
                    ASSET_MAP =
                        AssetRegistry.getAssetStore<String?, BedwarsGenerator?, DefaultAssetMap<String?, BedwarsGenerator?>?>(
                            BedwarsGenerator::class.java
                        ).getAssetMap()
                }
                return ASSET_MAP!!
            }
    }
}