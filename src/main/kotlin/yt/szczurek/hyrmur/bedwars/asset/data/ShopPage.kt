package yt.szczurek.hyrmur.bedwars.asset.data

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.codec.validation.Validators
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator

class ShopPage {
    var title: String = "Page 1"
        private set
    var icon: String = "Icons/ItemsGenerated/Weapon_Longsword_Mithril.png"
        private set
    var trades: Array<ShopTrade> = emptyArray()
        private set

    companion object {
        val CODEC: BuilderCodec<ShopPage> =
            BuilderCodec.builder(ShopPage::class.java, ::ShopPage)
                .append(
                    KeyedCodec("Title", Codec.STRING),
                    { page, title -> page.title = title },
                    { page -> page.title })
                .addValidator(Validators.nonEmptyString())
                .add()
                .append(
                    KeyedCodec("Icon", Codec.STRING),
                    { page, icon -> page.icon = icon },
                    { page -> page.icon })
                .addValidator(CommonAssetValidator.ICON_ITEM)
                .add()
                .append(
                    KeyedCodec("Trades",
                        ArrayCodec(ShopTrade.CODEC) { size -> arrayOfNulls(size) }
                    ),
                    { page, trades -> page.trades = trades },
                    { page -> page.trades })
                .add()
                .build()
    }
}