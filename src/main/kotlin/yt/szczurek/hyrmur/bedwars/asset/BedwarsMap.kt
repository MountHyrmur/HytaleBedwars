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


class BedwarsMap() : JsonAssetWithMap<String, DefaultAssetMap<String, BedwarsMap>> {
    var name: String? = null
        private set
    private var data: AssetExtraInfo.Data? = null
    var displayName: String? = null
    var instance: String? = null
    var teamCount: Int? = null

    constructor(name: String) : this() {
        this.displayName = name
        this.name = name
        this.instance = name
    }

    override fun getId(): String? {
        return name
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
            ), { team, string -> team.displayName = string }, { team -> team.displayName })
            .addValidator(Validators.nonEmptyString())
            .add()
            .append(
                KeyedCodec("Instance", Codec.STRING),
                { team, instance -> team.instance = instance },
                { team -> team.instance })
            .addValidator(Validators.nonNull())
            .addValidator(InstanceValidator.INSTANCE)
            .add()
            .append(
                KeyedCodec("TeamCount", Codec.INTEGER),
                { team, teamCount -> team.teamCount = teamCount },
                { team -> team.teamCount })
            .addValidator(Validators.greaterThan(0))
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