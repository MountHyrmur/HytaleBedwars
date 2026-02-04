package yt.szczurek.hyrmur.bedwars.asset

import com.hypixel.hytale.assetstore.AssetExtraInfo
import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.AssetStore
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap
import com.hypixel.hytale.builtin.instances.InstanceValidator
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.validation.Validators
import com.hypixel.hytale.server.core.asset.AssetModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import kotlin.io.path.Path


class BedwarsMap() : JsonAssetWithMap<String, DefaultAssetMap<String, BedwarsMap>> {
    var name: String? = null
        private set
    private var data: AssetExtraInfo.Data? = null
    var displayName: String? = null
    var instance: String? = null
    var chunkLoadRadius: Int = 6
    var playable: Boolean = false
    var teamCount: Int = 0

    constructor(name: String) : this() {
        this.displayName = name
        this.name = name
        this.instance = name
    }

    override fun getId(): String? {
        return name
    }

    fun saveToDisk() {
        val packName = assetMap.getAssetPack(name)!!
        val pack = AssetModule.get().getAssetPack(packName)

        val map = this

        BedwarsPlugin.get().scope.launch {
            withContext(Dispatchers.IO) {
                assetStore.writeAssetToDisk(pack,  mapOf(Path("$name.json") to map))
            }
        }
    }

    companion object {
        val CODEC: AssetBuilderCodec<String?, BedwarsMap> = AssetBuilderCodec.builder(
            BedwarsMap::class.java,
            { BedwarsMap() },
            Codec.STRING,
            { asset, id -> asset.name = id },
            { asset -> asset.name },
            { asset, data -> asset.data = data },
            { asset -> asset.data }).append(
                KeyedCodec(
                "DisplayName", Codec.STRING
            ), { map, string -> map.displayName = string }, { map -> map.displayName })
            .addValidator(Validators.nonEmptyString())
            .add()
            .append(
                KeyedCodec("Instance", Codec.STRING),
                { map, instance -> map.instance = instance },
                { map -> map.instance })
            .addValidator(Validators.nonNull())
            .addValidator(InstanceValidator.INSTANCE)
            .add()
            .append(
                KeyedCodec("ChunkLoadRadius", Codec.INTEGER),
                { map, chunkLoadRadius -> map.chunkLoadRadius = chunkLoadRadius },
                { map -> map.chunkLoadRadius })
            .addValidator(Validators.range(4, 24))
            .add()
            .append(
                KeyedCodec("Playable", Codec.BOOLEAN),
                { map, playable -> map.playable = playable },
                { map -> map.playable })
            .addValidator(Validators.nonNull())
            .add()
            .append(
                KeyedCodec("TeamCount", Codec.INTEGER),
                { map, teamCount -> map.teamCount = teamCount },
                { map -> map.teamCount })
            .add()
            .build()

        private var ASSET_STORE: AssetStore<String, BedwarsMap, DefaultAssetMap<String, BedwarsMap>>? = null
        val assetStore: AssetStore<String, BedwarsMap, DefaultAssetMap<String, BedwarsMap>>
            get() {
                if (ASSET_STORE == null) {
                    ASSET_STORE = AssetRegistry.getAssetStore(
                        BedwarsMap::class.java
                    )
                }
                return ASSET_STORE!!
            }
        val assetMap: DefaultAssetMap<String, BedwarsMap>
            get() {
                if (ASSET_STORE == null) {
                    ASSET_STORE = AssetRegistry.getAssetStore(
                        BedwarsMap::class.java
                    )
                }
                return ASSET_STORE!!.assetMap
            }
    }
}