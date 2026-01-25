package yt.szczurek.hyrmur.bedwars.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo
import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.validation.Validators
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil
import com.hypixel.hytale.server.core.codec.ProtocolCodecs


class BedwarsTeam : JsonAssetWithMap<String?, DefaultAssetMap<String?, BedwarsTeam?>?> {
    var name: String? = null
        private set
    private var data: AssetExtraInfo.Data? = null
    var displayName: String? = name
    var color: Color = DEFAULT_COLOR

    override fun getId(): String? {
        return name
    }

    companion object {
        val DEFAULT_COLOR: Color = ColorParseUtil.hexStringToColor("#FF0000")
        val CODEC: AssetBuilderCodec<String?, BedwarsTeam> =
            AssetBuilderCodec.builder(
                BedwarsTeam::class.java,
                { BedwarsTeam() },
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
                    KeyedCodec("Color", ProtocolCodecs.COLOR),
                    { team, color -> team.color = color },
                    { team -> team.color })
                .addValidator(Validators.nonNull())
                .add()
                .build()

        private var ASSET_MAP: DefaultAssetMap<String?, BedwarsTeam?>? = null
        val assetMap: DefaultAssetMap<String?, BedwarsTeam?>
            get() {
                if (ASSET_MAP == null) {
                    ASSET_MAP =
                        AssetRegistry.getAssetStore<String?, BedwarsTeam?, DefaultAssetMap<String?, BedwarsTeam?>?>(
                            BedwarsTeam::class.java
                        ).getAssetMap()
                }
                return ASSET_MAP!!
            }
    }
}