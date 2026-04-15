package yt.szczurek.hyrmur.bedwars.asset

import com.hypixel.hytale.assetstore.AssetExtraInfo
import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.codec.validation.Validators
import yt.szczurek.hyrmur.bedwars.asset.data.ShopPage

class BedwarsShop: JsonAssetWithMap<String?, DefaultAssetMap<String?, BedwarsShop>> {
    private var extraData: AssetExtraInfo.Data? = null
    private var id: String? = null
    var title: String = "Shop"
        private set
    var pages: Array<ShopPage> = emptyArray()
        private set

    override fun getId(): String? {
        return id
    }

    companion object {
        val CODEC: AssetBuilderCodec<String?, BedwarsShop> =
            AssetBuilderCodec.builder(
                BedwarsShop::class.java,
            { BedwarsShop() },
            Codec.STRING,
            { shop, id -> shop.id = id },
            { shop -> shop.id },
            { asset, data -> asset.extraData = data },
            { asset -> asset.extraData })
           .append(
                    KeyedCodec("Title", Codec.STRING),
                    { shop, title -> shop.title = title },
                    { shop -> shop.title })
                .addValidator(Validators.nonEmptyString())
                .add()
                .append(
                    KeyedCodec("Pages",
                        ArrayCodec(ShopPage.CODEC) { size -> arrayOfNulls(size) }
                    ),
                    { shop, pages -> shop.pages = pages },
                    { shop -> shop.pages })
                .add()
                .build()
        private var ASSET_MAP: DefaultAssetMap<String?, BedwarsShop>? = null
        val assetMap: DefaultAssetMap<String?, BedwarsShop>
            get() {
                if (ASSET_MAP == null) {
                    ASSET_MAP =
                        AssetRegistry.getAssetStore<String?, BedwarsShop, DefaultAssetMap<String?, BedwarsShop>>(
                            BedwarsShop::class.java
                        ).getAssetMap()
                }
                return ASSET_MAP!!
            }
    }
}