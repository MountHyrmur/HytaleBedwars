package yt.szczurek.hyrmur.bedwars.interaction

import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator
import yt.szczurek.hyrmur.bedwars.asset.BedwarsShop
import java.util.*

class BedwarsShopExistsValidator : AssetValidator {
    private constructor()

    private constructor(config: EnumSet<Config?>?) : super(config)

    override fun getDomain(): String {
        return "BedwarsShop"
    }

    override fun test(marker: String?): Boolean {
        return BedwarsShop.assetMap.getAsset(marker) != null
    }

    override fun errorMessage(marker: String, attributeName: String?): String {
        return "The barter shop asset with the name \"$marker\" does not exist for attribute \"$attributeName\""
    }

    override fun getAssetName(): String {
        return BedwarsShop::class.java.simpleName
    }

    companion object {
        private val DEFAULT_INSTANCE = BedwarsShopExistsValidator()

        fun required(): BedwarsShopExistsValidator {
            return DEFAULT_INSTANCE
        }
    }
}