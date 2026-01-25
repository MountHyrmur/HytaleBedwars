package yt.szczurek.hyrmur.bedwars.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo
import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap
import com.hypixel.hytale.builtin.instances.InstanceValidator
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.validation.Validators
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil
import com.hypixel.hytale.server.core.codec.ProtocolCodecs
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSingleValidator
import com.hypixel.hytale.server.npc.asset.builder.validators.IntValidator


class BedwarsMap : JsonAssetWithMap<String, DefaultAssetMap<String, BedwarsMap>> {
    var name: String? = null
        private set
    private var data: AssetExtraInfo.Data? = null
    var displayName: String? = null
    var instance: String? = null
    var teamCount: Int? = null

    override fun getId(): String? {
        return name
    }

    companion object {
        val CODEC: AssetBuilderCodec<String?, BedwarsMap> =
            AssetBuilderCodec.builder(
                BedwarsMap::class.java,
                { BedwarsMap() },
                Codec.STRING,
                { asset, id -> asset.name = id },
                { asset -> asset.name },
                { asset, data -> asset.data = data },
                { asset -> asset.data })
                .append(
                    KeyedCodec(
                        "DisplayName", Codec.STRING
                    ),
                    { team, string -> team.displayName = string },
                    { team -> team.displayName }
                )
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

        private var ASSET_MAP: DefaultAssetMap<String, BedwarsMap>? = null
        val assetMap: DefaultAssetMap<String, BedwarsMap>
            get() {
                if (ASSET_MAP == null) {
                    ASSET_MAP =
                        AssetRegistry.getAssetStore(
                            BedwarsMap::class.java
                        ).getAssetMap()
                }
                return ASSET_MAP!!
            }
    }
}