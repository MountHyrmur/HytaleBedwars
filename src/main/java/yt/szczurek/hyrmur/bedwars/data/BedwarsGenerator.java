package yt.szczurek.hyrmur.bedwars.data;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDefaultCollapsedState;

import java.util.Arrays;

public class BedwarsGenerator implements JsonAssetWithMap<String, DefaultAssetMap<String, BedwarsGenerator>> {
    public static final AssetBuilderCodec<String, BedwarsGenerator> CODEC = AssetBuilderCodec.builder(
                    BedwarsGenerator.class,
                    BedwarsGenerator::new,
                    Codec.STRING,
                    (generator, id) -> generator.id = id,
                    generator -> generator.id,
                    (asset, data) -> asset.data = data,
                    asset -> asset.data
            )
            .append(
                    new KeyedCodec<>("Drops", new ArrayCodec<>(GeneratorDropEntry.CODEC, GeneratorDropEntry[]::new)),
                    (generator, l) -> generator.drops = l,
                    generator -> generator.drops
            )
            .metadata(UIDefaultCollapsedState.UNCOLLAPSED)
            .add()
            .build();


    private static DefaultAssetMap<String, BedwarsGenerator> ASSET_MAP;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected GeneratorDropEntry[] drops;

    public static DefaultAssetMap<String, BedwarsGenerator> getAssetMap() {
        if (ASSET_MAP == null) {
            ASSET_MAP = AssetRegistry.getAssetStore(BedwarsGenerator.class).getAssetMap();
        }
        return ASSET_MAP;
    }

    public String getId() {
        return id;
    }

    public GeneratorDropEntry[] getDrops() {
        return drops;
    }

    public int maxLevel() {
        return Arrays.stream(drops).map(entry -> entry.cooldownsByLevel.length).min(Integer::compare).orElseThrow() - 1;
    }
}
